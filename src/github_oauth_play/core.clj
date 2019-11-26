(ns github-oauth-play.core
  (:require [clojure.pprint]
            [clojure.data.json :as json]
            [org.httpkit.server :as http-server]
            [org.httpkit.client :as http-client]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [tech.config.core :as config]
            [hiccup.page :as hiccup])
  (:gen-class))

(defonce server-stop-fn* (atom nil))

(defn home-page
  []
  (hiccup/html5
   [:head]
   [:body
    [:p "Well, hello there!"]
    [:p "We're going to now talk to the GitHub API. Ready? "
     [:a {:href (str "https://github.com/login/oauth/authorize?scope=user:email&client_id="
                     (config/get-config :client-id))}
      "Click here"]
     " to begin!"]]))

(defn code->token
  [code]
  (let [{:keys [status error body]}
        @(http-client/post "https://github.com/login/oauth/access_token"
                           {:form-params {"client_id" (config/get-config :client-id)
                                          "client_secret" (config/get-config :client-secret)
                                          "code" code}
                            :headers {"Accept" "application/json"}})]
    (if (= 200 status)
      (let [response (json/read-str body)]
        (get response "access_token"))
      (throw (Exception. error)))))

(defn token->emails
  [token]
  (let [{:keys [status error body]}
        @(http-client/get "https://api.github.com/user/emails"
                          {:query-params {"access_token" token}
                           :headers {"Accept" "application/json"}})]
    (if (= 200 status)
      (let [response (json/read-str body)]
        response)
      (throw (Exception. error)))))

(defn callback-page
  [req]
  (let [code (get-in req [:params "code"])]
    (try (if-let [emails (->> code code->token token->emails)]
           {:status  200
            :headers {"Content-Type" "text/plain"}
            :body    (with-out-str (clojure.pprint/print-table emails))}
           (response/redirect "/"))
         (catch Throwable e
           (clojure.pprint/pprint e)
           (response/redirect "/")))))

(defn app
  [req]
  (condp = (:uri req)
    "/callback" (callback-page req)
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (home-page)}))

(defn stop
  []
  (when-not (nil? @server-stop-fn*)
    (@server-stop-fn* :timeout 100)
    (reset! server-stop-fn* nil)))

(defn restart
  []
  (stop)
  (let [port 4567
        handler (-> #'app wrap-params)]
    (reset! server-stop-fn* (http-server/run-server handler {:port port}))
    (println "Started server on port:" port)))

(defn -main [& args]
  (restart))
