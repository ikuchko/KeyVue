var imageList = [];
var parentNode = "";
var currentNode = "";
var page = 0;
var alpha = 1; //amount of not image files to subtract from nodeId

$(function() {
  $('body').on('contextmenu', '.tiff-image', function(e){ return false; });

  $(window).keypress(function(e) {
    var ev = e || window.event;
    var key = ev.keyCode || ev.which;
    zoomImage(key);
  });
  $(window).keydown(function(e) {
    var ev = e || window.event;
    var key = ev.keyCode || ev.which;
    if (ev.ctrlKey) {
      switch (key) {
        case 37: $('#page-previous').click();
          break;
        case 39: $('#page-next').click();
          break;
        case 38: $('#image-up').click();
          break;
        case 40: $('#image-down').click();
          break;
      }
    } else {
      var delta = 50;
      switch (key) {
        case 37: moveImage(-delta, 0);
        e.preventDefault();
        break;
        case 39: moveImage(delta, 0);
        e.preventDefault();
        break;
        case 38: moveImage(0, -delta);
        e.preventDefault();
        break;
        case 40: moveImage(0, delta);
        e.preventDefault();
        break;
      }
    };
  });

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
                  $('#tree').data('treeview').addNode(data.nodeId, JSON.parse(response));
                  // $('#tree').treeview('addNode', data.nodeId, JSON.parse(response));

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
    if (currentNode) {
      var userLogin = $('#userLogin').val();
      var parentName = $('#tree').treeview('getParent', currentNode).text;
      $('#progress-bar').showV();
      page++;
      loadImage(userLogin, parentName, currentNode.files[page].imageName);
    }
  });
  $('#page-previous').on("click", function() {
    if (currentNode) {
      var userLogin = $('#userLogin').val();
      var parentName = $('#tree').treeview('getParent', currentNode).text;
      $('#progress-bar').showV();
      page--;
      loadImage(userLogin, parentName, currentNode.files[page].imageName);
    }
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
    setImageZooming(canvas.toDataURL());

    moveImage();
    var targetNode = document.querySelector (".tiff-image");
    triggerMouseEvent (targetNode, "mousedown");
    triggerMouseEvent (targetNode, "mouseup");
    function triggerMouseEvent (node, eventType) {
      var clickEvent = document.createEvent ('MouseEvents');
      clickEvent.initEvent (eventType, true, true);
      node.dispatchEvent (clickEvent);
    }


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

function moveImage(x,y) {
  var canvas = document.getElementsByTagName('canvas')[0];
  if (canvas) {
    var ctx = canvas.getContext('2d');
    ctx.translate(x,y);
    var event = new Event('redraw');
    canvas.dispatchEvent(event);
  }
}

function zoomImage(key) {
  var canvas = document.getElementsByTagName('canvas')[0];
  if (canvas && (key == 43 || key == 45)) {
    var ctx = canvas.getContext('2d');
    var event = new CustomEvent('zoom',
      {
        detail: {
          clicks: (key === 43 ? 1 : -1),
        },
        bubbles: true,
  		  cancelable: true
      });
    canvas.dispatchEvent(event);
  }
}


function setImageZooming(imageSource) {
  var canvas = document.getElementsByTagName('canvas')[0];
  canvas.width = 800; canvas.height = 1000;
  var tiffImage = new Image;
  var ctx = canvas.getContext('2d');
  trackTransforms(ctx);
  function redraw(){
    // Clear the entire canvas
    var p1 = ctx.transformedPoint(0,0);
    // var p2 = ctx.transformedPoint(canvas.width,canvas.height);
    var p2 = ctx.transformedPoint(canvas.width, canvas.height);
    ctx.clearRect(p1.x,p1.y,p2.x-p1.x,p2.y-p1.y);

    ctx.drawImage(tiffImage,0,0);

    ctx.beginPath();
    ctx.lineWidth = 6;
    ctx.moveTo(399,250);
    ctx.lineTo(474,256);

    ctx.save();
    ctx.translate(4,2);
    ctx.beginPath();
    ctx.lineWidth = 1;
    ctx.moveTo(436,253);
    ctx.lineTo(437.5,233);

    ctx.save();
    ctx.translate(438.5,223);
    ctx.strokeStyle = '#06c';
    ctx.beginPath();
    ctx.lineWidth = 0.05;
    for (var i=0;i<60;++i){
      ctx.rotate(6*i*Math.PI/180);
      ctx.moveTo(9,0);
      ctx.lineTo(10,0);
      ctx.rotate(-6*i*Math.PI/180);
    }
    ctx.restore();

    ctx.beginPath();
    ctx.lineWidth = 0.2;
    ctx.arc(438.5,223,10,0,Math.PI*2);
    ctx.restore();
  }
  redraw();

  var lastX=canvas.width/2, lastY=canvas.height/2;
  var dragStart,dragged;
  canvas.addEventListener('mousedown',function(evt){
    document.body.style.mozUserSelect = document.body.style.webkitUserSelect = document.body.style.userSelect = 'none';
    lastX = evt.offsetX || (evt.pageX - canvas.offsetLeft);
    lastY = evt.offsetY || (evt.pageY - canvas.offsetTop);

    dragStart = ctx.transformedPoint(lastX,lastY);
    dragged = false;
  },false);
  canvas.addEventListener('mousemove',function(evt){
    lastX = evt.offsetX || (evt.pageX - canvas.offsetLeft);
    lastY = evt.offsetY || (evt.pageY - canvas.offsetTop);
    dragged = true;
    if (dragStart){
      var pt = ctx.transformedPoint(lastX,lastY);
      ctx.translate(pt.x-dragStart.x,pt.y-dragStart.y);
      redraw();
    }
  },false);
  canvas.addEventListener('mouseup',function(evt){
    dragStart = null;
    if (!dragged) zoom(evt.button === 2 ? -1 : 1 );
  },false);
  canvas.addEventListener('redraw',function(){
    redraw();
  },false);
  canvas.addEventListener('zoom',function(evt){
    zoom(evt.detail.clicks);
  },false);

  var scaleFactor = 1.1;
  var zoom = function(clicks){
    var pt = ctx.transformedPoint(lastX,lastY);
    ctx.translate(pt.x,pt.y);
    var factor = Math.pow(scaleFactor,clicks);
    ctx.scale(factor,factor);
    ctx.translate(-pt.x,-pt.y);
    redraw();
  }

  var resizeImage = function() {
    var pt = ctx.transformedPoint(lastX,lastY);
    var factor = 0.3;
    ctx.scale(factor,factor);
    ctx.translate(-200,0);
    redraw();
  }
  resizeImage();

  var handleScroll = function(evt){
    var delta = evt.wheelDelta ? evt.wheelDelta/40 : evt.detail ? -evt.detail : 0;
    if (delta) zoom(delta);
    return evt.preventDefault() && false;
  };
  canvas.addEventListener('DOMMouseScroll',handleScroll,false);
  canvas.addEventListener('mousewheel',handleScroll,false);

  tiffImage.src = imageSource;
  // tiffImage.src = 'http://phrogz.net/tmp/alphaball.png';
  // ball.src   = 'http://phrogz.net/tmp/alphaball.png';

  // Adds ctx.getTransform() - returns an SVGMatrix
  // Adds ctx.transformedPoint(x,y) - returns an SVGPoint
  function trackTransforms(ctx){
		var svg = document.createElementNS("http://www.w3.org/2000/svg",'svg');
		var xform = svg.createSVGMatrix();
		ctx.getTransform = function(){ return xform; };

		var savedTransforms = [];
		var save = ctx.save;
		ctx.save = function(){
			savedTransforms.push(xform.translate(0,0));
			return save.call(ctx);
		};
		var restore = ctx.restore;
		ctx.restore = function(){
			xform = savedTransforms.pop();
			return restore.call(ctx);
		};

		var scale = ctx.scale;
		ctx.scale = function(sx,sy){
			xform = xform.scaleNonUniform(sx,sy);
			return scale.call(ctx,sx,sy);
		};
		var rotate = ctx.rotate;
		ctx.rotate = function(radians){
			xform = xform.rotate(radians*180/Math.PI);
			return rotate.call(ctx,radians);
		};
		var translate = ctx.translate;
		ctx.translate = function(dx,dy){
			xform = xform.translate(dx,dy);
			return translate.call(ctx,dx,dy);
		};
		var transform = ctx.transform;
		ctx.transform = function(a,b,c,d,e,f){
			var m2 = svg.createSVGMatrix();
			m2.a=a; m2.b=b; m2.c=c; m2.d=d; m2.e=e; m2.f=f;
			xform = xform.multiply(m2);
			return transform.call(ctx,a,b,c,d,e,f);
		};
		var setTransform = ctx.setTransform;
		ctx.setTransform = function(a,b,c,d,e,f){
			xform.a = a;
			xform.b = b;
			xform.c = c;
			xform.d = d;
			xform.e = e;
			xform.f = f;
			return setTransform.call(ctx,a,b,c,d,e,f);
		};
		var pt  = svg.createSVGPoint();
		ctx.transformedPoint = function(x,y){
			pt.x=x; pt.y=y;
			return pt.matrixTransform(xform.inverse());
		}
	}

}
