$(function() {
  var treeViewData;
  $('#login-form').submit(function(event){
    event.preventDefault();
    verifyUser($('#login').val(), $('#password').val(), event);
  });

  if($('#tree').length > 0) {
    $.get("/getList", {user: $('#userLogin').val()}, function(list) {
      treeViewData = JSON.parse(list);
      $('#tree').treeview({data: treeViewData});
    });
  }

//  var xhr = new XMLHttpRequest();
//  console.log("Start loading");
//  xhr.responseType = 'arraybuffer';
//  xhr.open('GET', "/2016021200.001");
//  xhr.onload = function ( e ) {
//    Tiff.initialize({TOTAL_MEMORY: 133554432 })
//     var tiff = new Tiff({buffer: xhr.response});
//     var canvas = tiff.toCanvas();
//     $(canvas).addClass("tiff-image");
//     $(".scrollbox").append(canvas);
//     console.log("End loading");
//  };
//  xhr.send();
  
  $('#button').click(function() {
    $.get("/getList", {user: $('#userLogin').val()}, function(list) {
      a = JSON.parse(list);
      treeViewData[2]["nodes"] = a;
      $('#tree').treeview({data: treeViewData});
    });      
  })
  
});

function getTree() {
  var tree = [
    {
      text: "Firs node 1",
      nodes: [
        {
          text: "WTF 1",
          nodes: [
            {
              text: "Grandchild 1"
            },
            {
              text: "Grandchild 2"
            }
          ]
        },
        {
          text: "Child 2"
        }
      ]
    },
    {
      text: "Parent 2"
    },
    {
      text: "Parent 3"
    },
    {
      text: "Parent 4"
    },
    {
      text: "Parent 5"
    }
  ];
  return tree;
}

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
