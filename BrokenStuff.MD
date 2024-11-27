# Broken stuff

## v1/completitions
https://docs.x.ai/api/endpoints#completions
* prompt is not correctly documented
* logit_bias is not clear
* logprobs looks more like Int and than Boolean
* stream_options is not documented

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
