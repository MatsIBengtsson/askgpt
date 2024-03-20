package io.nerdythings.model

import com.google.gson.annotations.SerializedName

data class ActionRequest(
    @SerializedName("project_name")
    val projectName: String?,
    @SerializedName("presentation_name")
    val presentationName: String?,
    @SerializedName("presentation_description")
    val presentationDescription: String?,
    @SerializedName("element_type")
    val elementType: String?,
    @SerializedName("code")
    val code: String?,
    @SerializedName("local_file_path")
    val localFilePath: String?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("element_context")
    val elementContext: String?,
)

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

