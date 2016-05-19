var imageList = [];
var parentNode = "";
var currentNode = "";
var page = 0;
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
                $(this).treeview('toggleNodeExpanded',data.nodeId).treeview('unselectNode', data.nodeId);
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
              loadImage(userLogin, parentName, data.files[0].imageName)
        	  }
        	}
        });
      };
    });
  }

  //navbar event handler
  $('#image-up').on("click", function() {
    var nextNode = $('#tree').treeview('getNode', currentNode.nodeId + 1);
    if (nextNode.type === "tif") {
      $('#progress-bar').showV();
      $('#tree').treeview('selectNode', [ nextNode, { silent: false } ]);
    }
  });
  $('#image-down').on("click", function() {
    var nextNode = $('#tree').treeview('getNode', currentNode.nodeId - 1);
    if (nextNode.type === "tif") {
      $('#progress-bar').showV();
      $('#tree').treeview('selectNode', [ nextNode, { silent: false } ]);
    }
  });
  $('#page-next').on("click", function() {
    var userLogin = $('#userLogin').val();
    var parentName = $('#tree').treeview('getParent', currentNode).text;
    $('#progress-bar').showV();
    page++;
    loadImage(userLogin, parentName, currentNode.files[page].imageName);
  });
  $('#page-previous').on("click", function() {
    var userLogin = $('#userLogin').val();
    var parentName = $('#tree').treeview('getParent', currentNode).text;
    $('#progress-bar').showV();
    page--;
    loadImage(userLogin, parentName, currentNode.files[page].imageName);
  });

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
  page = 0;
  $('#pageLabel').hideV();
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
    $('#page-next').disable(true);
    $('#page-previous').disable(true);
  } else {
    var node = options.node;
    if ($('#tree').treeview('getParent', node).text !== parentNode.text) {
      imageList = getImageList(node);
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

function loadImage(userLogin, parentName, fileName) {
  var xhr = new XMLHttpRequest();
  $('#progress-bar').showV();
  console.log("Start loading");
  xhr.responseType = 'arraybuffer';
  xhr.open('get', "/temp/" + userLogin + "/" + parentName + "/" + fileName);
  xhr.onload = function ( e ) {
    Tiff.initialize({TOTAL_MEMORY: 133554432 })
    var tiff = new Tiff({buffer: xhr.response});
    $(".tiff-image").remove();
    var canvas = tiff.toCanvas();
    $(canvas).addClass("tiff-image");
    $(".scrollbox").append(canvas);
    $('#progress-bar').hideV();
    updatePageState();
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

function getImageList(node) {
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

function updatePageState() {
  $('#pageLabel').showV();
  $('#pageLabel').text("PAGE " + (page + 1) + " OF " + currentNode.files.length);
  if (page === 0) {
    $('#page-previous').disable(true);
  } else {
    $('#page-previous').disable(false);
  }
  if (page === currentNode.files.length - 1) {
    $('#page-next').disable(true);
  } else {
    $('#page-next').disable(false);
  }
}

jQuery.fn.showV = function() {
    this.css('visibility', 'visible');
}

jQuery.fn.hideV = function() {
    this.css('visibility', 'hidden');
}
