$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });

  // $.get( "/tiff", function( data ) {
  //   var parentEl = $(this).parent();
  //   alert("Response here: " + data);
  //   var b64Response = btoa(data);
  //
  //   // create an image
  //   var outputImg = document.createElement('img');
  //   outputImg.src = 'data:image/png;base64,'+b64Response;
  //
  //   // append it to your page
  //   document.body.appendChild(outputImg);
  //
  //   // var filrR = new FileReader();
  //   // filrR.onload = function(e) {
  //   //   $(parentEl).append('<img src="' + filrR.result + '" class="preview"/>');
  //   // };
  //   // filrR.readAsDataURL(data);
  //
  //   // $(parentEl).append('<img src="' + fr.result + '" class="preview"/>');
  // });

  var xhr = new XMLHttpRequest();
  xhr.responseType = 'arraybuffer';
  xhr.open('GET', "/2016021200.001");
  xhr.onload = function ( e ) {
    Tiff.initialize({TOTAL_MEMORY: 133554432 })
     var tiff = new Tiff({buffer: xhr.response});
     var canvas = tiff.toCanvas();
     console.log(canvas);
     document.body.appendChild(canvas);
  };
  xhr.send();


  // $.get( "/tiff", function( data ) {
  //   var parentEl = $(this).parent();
  //   alert(data.header);
  //   var fr = new FileReader();
  //   fr.onload = function(e) {
  //     //Using tiff.min.js library - https://github.com/seikichi/tiff.js/tree/master
  //     console.debug("Parsing TIFF image...");
  //     //initialize with 100MB for large files
  //     Tiff.initialize({
  //       TOTAL_MEMORY: 100000000
  //     });
  //     var tiff = new Tiff({
  //       buffer: e.target.result
  //     });
  //     var tiffCanvas = tiff.toCanvas();
  //     $(tiffCanvas).css({
  //       "max-width": "1000000px",
  //       "width": "100%",
  //       "height": "auto",
  //       "display": "block",
  //       "padding-top": "10px"
  //     }).addClass("preview");
  //     $(parentEl).append(tiffCanvas);
  //   }
  //
  //   fr.onloadend = function(e) {
  //     console.debug("Load End");
  //   }
  //   var blob = new Blob([data], {type: 'image/tiff'});
  //   var arrayBuffer = this.response;
  //      var file = new File({buffer: arrayBuffer}, "123.tif");
  //     fr.readAsArrayBuffer(file);
  // });

  var fileTypes = ['jpg', 'jpeg', 'png', 'tiff', 'tif', 'pdf']; //acceptable file types
  $("input:file").change(function(evt) {
  // $.get( "/tiff", function( evt ) {
    var parentEl = $(this).parent();
    $(this).parent().find("img.preview").remove();
    $(this).parent().find("canvas.preview").remove();
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
