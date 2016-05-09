$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val());
  });
});

function verifyUser(login, password) {
  $.get( "/transfer", {user: login, pass: password}, function( data ) {
    if (data == "true") {
      alert ("we got TRUE :" + data);
    } else {
      alert("we got FALSE :" + data);
    }
});
}
