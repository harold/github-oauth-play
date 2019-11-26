# github-oauth-play

A Clojure application for playing with github oauth

## Usage

Expects `resources/user-config.edn` like:

```
{:client-id "REDACTED"
 :client-secret "REDACTEDREDACTED"}
```

`lein run`

or

`lein repl` -> `github-oauth-play.core/-main`

## License

Copyright © 2019 Harold