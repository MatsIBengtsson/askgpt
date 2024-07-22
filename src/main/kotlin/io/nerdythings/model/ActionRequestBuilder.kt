package io.nerdythings.model

data class ActionRequestBuilder(
    var projectName: String? = null,
    var presentationName: String? = null,
    var presentationDescription: String? = null,
    var elementType: String? = null,
    var fileContent: String? = null,
    var localFilePath: String? = null,
    var language: String? = null,
    var elementContext: String? = null,
) {
    fun build(): ActionRequest {
        return ActionRequest(
            projectName,
            presentationName,
            presentationDescription,
            elementType,
            fileContent,
            localFilePath,
            language,
            elementContext,
        )
    }
}