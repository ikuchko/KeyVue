$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });

  // $.get( "/tiff", function( data ) {
  //   alert("Response here: " + data);
  // });

  var fileTypes = ['jpg', 'jpeg', 'png', 'tiff', 'tif', 'pdf']; //acceptable file types
  $("input:file").change(function(evt) {
  // $.get( "/tiff", function( evt ) {
    var parentEl = $(this).parent();
    $(this).parent().find("img.preview").remove();
    $(this).parent().find("canvas.preview").remove();
    debugger;
    var tgt = evt.target || window.event.srcElement,
      files = tgt.files;
    // FileReader support
    if (FileReader && files && files.length) {
      var fr = new FileReader();
      var extension = files[0].name.split('.').pop().toLowerCase();
      var tif = false;
      var pdf = false;
      if (extension == "tiff" || extension == "tif")
        tif = true;
      else if (extension == "pdf")
        pdf = true;
      fr.onload = function(e) {
        success = fileTypes.indexOf(extension) > -1;
        if (success) {
          if (tif) {
            //Using tiff.min.js library - https://github.com/seikichi/tiff.js/tree/master
            console.debug("Parsing TIFF image...");
            //initialize with 100MB for large files
            Tiff.initialize({
              TOTAL_MEMORY: 100000000
            });
            var tiff = new Tiff({
              buffer: e.target.result
            });
            var tiffCanvas = tiff.toCanvas();
            $(tiffCanvas).css({
              "max-width": "1000000px",
              "width": "100%",
              "height": "auto",
              "display": "block",
              "padding-top": "10px"
            }).addClass("preview");
            $(parentEl).append(tiffCanvas);
          } else {
            console.debug("render immmm");
            $(parentEl).append('<img src="' + fr.result + '" class="preview"/>');
          }
        }

      }

      fr.onloadend = function(e) {
        console.debug("Load End");
      }
      if (tif)
        fr.readAsArrayBuffer(files[0]);
      else
        fr.readAsDataURL(files[0]);
    }
    // Not supported
    else {
      // fallback -- perhaps submit the input to an iframe and temporarily store
      // them on the server until the user's session ends.
    }
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
