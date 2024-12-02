# Known Issues with XAI backend

* The response object documentation is missing for all APIs.

## v1/completitions
https://docs.x.ai/api/endpoints#completions
* `prompt` is not correctly documented
* `logprobs` looks more like Int? than Boolean? as per the documentation
* `stream_options` is not documented

## v1/language-models
https://docs.x.ai/api/endpoints#list-language-models
* Response is not documented
* No example is provided

## v1/language-models/{model-id}
https://docs.x.ai/api/endpoints#get-language-model
* Document is wrong as it states embedding model but it is actually for language model

## v1/models
https://docs.x.ai/api/endpoints#list-models
* Response is not documented
* Example is incorrect `"models"` is actually `"data"`
* Additional `"object" : "list"` is return

## v1/chat/completions
https://docs.x.ai/api/endpoints#chat-completions
* `reponse_format` and `stream_options` are not documented
* Some images messages return an error `412 Precondition Failed`. They are not documented but experimentation shows that images need to be smaller - maybe < 1000x1000. The server doesn't auto resize yet.