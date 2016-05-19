$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });

  // Document page checking
  if($('#tree').length > 0) {
    updateNavState({init: true});
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
                updateNavState(node, page, 0)
                $('#progress-bar').hideV();
              } else {
                $.get("/getFolderContent", {user: userLogin, fileName: data.text}, function(response) {
                  console.log(JSON.parse(response));
                  $('#tree').data('treeview').addNode(data.nodeId, JSON.parse(response));
                  // $('#tree').treeview('addNode', data.nodeId, JSON.parse(response));
                  // debugger;
                  // treeViewData[data.nodeId].nodes = JSON.parse(response);
                  // renderTreeview(treeViewData);
                  $('#tree').treeview('expandNode', [ data.nodeId, { levels: 2, silent: true } ]);
                  $('#progress-bar').hideV();
                });
              }
        	  }
        	  if (data.type === "tif") {
              updateNavState({node: data});
              var parentName = $('#tree').treeview('getParent', data).text;
      		    var xhr = new XMLHttpRequest();
              $('#progress-bar').showV();
      		    console.log("Start loading");
      		    xhr.responseType = 'arraybuffer';
      		    xhr.open('get', "/temp/" + userLogin + "/" + parentName + "/" + data.files[0].imageName);
      		    xhr.onload = function ( e ) {
        		    Tiff.initialize({TOTAL_MEMORY: 133554432 })
      		      var tiff = new Tiff({buffer: xhr.response});
                $(".tiff-image").remove();
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

var imageList = [];
var parentNodeName = "";
var alpha = 1; //amount of not image files to subtract from nodeId
// options.init - make navbar clear and buttons disabled
function updateNavState(options) {
  if (options.init) {
    $('#image-up').addClass('disabled');
    $('#image-down').addClass('disabled');
    $('#previousImage').val("");
    $('#currentImage').val("");
    $('#nextImage').val("");
  } else {
    var node = options.node;
    console.log("node:");
    console.log(node);
    console.log($('#tree').treeview('getParent', node).text);
    console.log(parentNodeName);
    if ($('#tree').treeview('getParent', node).text !== parentNodeName) {
      imageList = getImages(node);
    }
    debugger;
    // var nodeId = node.nodeId - alpha;
    $('#currentImage').val(node.text);
    if (node.text === imageList[0].text) {
      $('#image-down').addClass('disabled');
      $('#image-up').removeClass('disabled');
      $('#previousImage').val("");
    } else if (imageList.length > 1) {
      if (node.text === imageList[imageList.length-1].text) {
        $('#image-up').addClass('disabled');
        $('#image-down').removeClass('disabled');
        $('#nextImage').val("");
      }
    }{
      $('#image-down').removeClass('disabled');
      $('#previousImage').val(imageList[nodeId - 1].text);
    }

    if (nodeId === imageList.length - 1) {
      $('#image-up').addClass('disabled');
      $('#nextImage').val("");
    } else {
      debugger;
      $('#image-up').removeClass('disabled');
      $('#nextImage').val(imageList[nodeId + 1].text);
    }
  }
}

function getImages(node) {
  parentNodeName = $('#tree').treeview('getParent', node);
  var result = [];
  var counter = 0;
  alpha = 1;
  for (var i=0; i<parentNodeName.nodes.length; i++) {
    if (parentNodeName.nodes[i].type === "tif") {
      result[counter] = parentNodeName.nodes[i];
      counter++;
    } else {
      alpha++;
    }
  }
  return result;
}

jQuery.fn.showV = function() {
    this.css('visibility', 'visible');
}

jQuery.fn.hideV = function() {
    this.css('visibility', 'hidden');
}
