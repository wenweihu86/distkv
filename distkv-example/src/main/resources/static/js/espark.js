// bind the on-change event for the input element (triggered when a file
// is chosen)
$(document).ready(function () {
    $("#upload-file-input").on("change", uploadFile);
});

/**
 * Upload the file sending it via Ajax at the Spring Boot server.
 */
function uploadFile() {
    $.ajax({
        url: "/upload",
        type: "POST",
        data: new FormData($("#upload-file-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        cache: false,
        success: function (data) {
            // Handle upload success
            $("#upload-file-message").text(data.body).addClass("alert alert-success");
        },
        error: function () {
            // Handle upload error
            $("#upload-file-message").text("File Upload Operation is UnSuccessful").addClass("alert alert-danger");
        }
    });
}