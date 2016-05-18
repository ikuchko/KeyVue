$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });

  // Document page checking
  if($('#tree').length > 0) {
	var userLogin = $('#userLogin').val();
    $.get("/getList", {user: userLogin}, function(list) {
      renderTreeview(JSON.parse(list));
      function renderTreeview (treeViewData) {
        $('#tree').treeview({
        	data: treeViewData,
        	levels: 1,
        	onNodeSelected: function(event, data) {
        	  if (data.type === "dir") {
              $('#progress-bar').showV();
              if (data.nodes) {
                $(this).treeview('toggleNodeExpanded',data.nodeId).treeview('unselectNode',data.nodeId);
                $('#progress-bar').hideV();
              } else {
                $.get("/getFolderContent", {user: userLogin, fileName: data.text}, function(response) {
                  console.log(JSON.parse(response));
                  treeViewData[data.nodeId].nodes = JSON.parse(response);
                  renderTreeview(treeViewData);
                  $('#tree').treeview('expandNode', [ data.nodeId, { levels: 2, silent: true } ]);
                  $('#progress-bar').hideV();
                });
              }
        	  }
        	  if (data.type === "tif") {
            var parentName = $('#tree').treeview('getParent', data).text;
    		    var xhr = new XMLHttpRequest();
            $('#progress-bar').showV();
    		    console.log("Start loading");
    		    xhr.responseType = 'arraybuffer';
    		    xhr.open('get', "/temp/" + userLogin + "/" + parentName + "/" + data.files[0].imageName);
    		    xhr.onload = function ( e ) {
      		    Tiff.initialize({TOTAL_MEMORY: 133554432 })
    		      var tiff = new Tiff({buffer: xhr.response});
    		      var canvas = tiff.toCanvas();
    		      $(canvas).addClass("tiff-image");
    		      $(".scrollbox").append(canvas);
              $('#progress-bar').hideV();
    		      console.log("End loading");
    		    };
            xhr.onerror = function ( e ) {
              $('#progress-bar').hideV();
              console.log("Error was catched during loading image");
            };
            xhr.onreadystatuschange = function () {
              debugger;
              if (xhr.readyState == 4 && xhr.status == 400) {
                $('#progress-bar').hideV();
                console.log("Error was catched during loading image");
              };
            }
    		    xhr.send();
        	  }
        	}
        });
      };
    });
  }

  $('#button').click(function() {
    $.get("/getList", {user: $('#userLogin').val()}, function(list) {
      a = JSON.parse(list);
      treeViewData[2]["nodes"] = a;
      $('#tree').treeview({data: treeViewData});
      $('#progress-bar').hideV();
    });
  })

});

function verifyUser(login, password, event) {
  $.get( "/verifyUser", {user: login, pass: password}, function( data ) {
    if (data == "true") {
      userLogin = login;
      event.currentTarget.submit();
    } else {
      $('#auth-error').show();
    }
  });
}

jQuery.fn.showV = function() {
    this.css('visibility', 'visible');
}

jQuery.fn.hideV = function() {
    this.css('visibility', 'hidden');
}
