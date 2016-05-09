$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });
});

function verifyUser(login, password, event) {
  $.get( "/verifyUser", {user: login, pass: password}, function( data ) {
    if (data == "true") {
      // $.get("/documents", function(documentPageResponse) {
      //   alert(documentPageResponse);
      // });
      event.currentTarget.submit();
    } else {
      $('#auth-error').show();
    }
});
}
