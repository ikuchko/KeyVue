var imageList = [];
var parentNode = "";
var currentNode = "";
var alpha = 1; //amount of not image files to subtract from nodeId

$(function() {
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });

  //disabling buttons
  jQuery.fn.extend({
    disable: function(state) {
      return this.each(function() {
        this.disabled = state;
      });
    }
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
                updateNavState({init: true});
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
              currentNode = data;
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

  //navbar event handler
  $('button').on("click", function() {
    if ($(this).attr('id') === "image-up") {
      var nextNode = $('#tree').treeview('getNode', currentNode.nodeId + 1);
    } else if ($(this).attr('id') === "image-down") {
      var nextNode = $('#tree').treeview('getNode', currentNode.nodeId - 1);
    }
    if (nextNode.type === "tif") {
      $('#tree').treeview('selectNode', [ nextNode, { silent: false } ]);
    }
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

// options.init - make navbar clear and buttons disabled
function updateNavState(options) {
  if (options.init) {
    imageList = [];
    parentNode = "";
    currentNode = "";
    alpha = 1;
    $('#image-up').disable(true);
    $('#image-down').disable(true);
    $('#previousImage').hideV();
    $('#currentImage').hideV();
    $('#nextImage').hideV();
  } else {
    var node = options.node;
    if ($('#tree').treeview('getParent', node).text !== parentNode.text) {
      imageList = getImages(node);
    }
    // if doucemnt first or last in the list - disable
    $('#currentImage').text(node.text);
    $('#currentImage').showV();
    if (node.nodeId === imageList[0].nodeId) {
      $('#image-down').disable(true);
      $('#previousImage').hideV();
    } else {
      $('#image-down').disable(false);
      $('#previousImage').text(getNode(node, -1).text);
      $('#previousImage').showV();
    }
    if (node.nodeId === imageList[imageList.length-1].nodeId) {
      $('#image-up').disable(true);
      $('#nextImage').hideV();
    } else {
      $('#image-up').disable(false);
      $('#nextImage').text(getNode(node, 1).text);
      $('#nextImage').showV();
    }

  }
}

function getNode(currentNode, position) {
  for (var i=0; i<imageList.length; i++) {
    if (imageList[i].nodeId === currentNode.nodeId) {
      return imageList[i + position];
    }
  }
}

function getImages(node) {
  parentNode = $('#tree').treeview('getParent', node);
  var result = [];
  var counter = 0;
  alpha = 1;
  for (var i=0; i<parentNode.nodes.length; i++) {
    if (parentNode.nodes[i].type === "tif") {
      result[counter] = parentNode.nodes[i];
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
