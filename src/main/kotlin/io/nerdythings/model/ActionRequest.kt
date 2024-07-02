/* This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
*/
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

