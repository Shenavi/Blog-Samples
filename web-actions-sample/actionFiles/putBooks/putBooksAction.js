var dbUtils = require('./dbUtils.js');

function main(args) {
    var badArgMsg = validateArgs(args);
    if (badArgMsg) {
        var result = {};
        result.message = badArgMsg;
        return {
           headers: {
               'Content-Type':'application/json'
           },
           statusCode:404,
           body: new Buffer(JSON.stringify(result)).toString('base64')
        }
    }

    var bookId = "";
    var dbUrl = args.dbUrl;
    var dbName = args.dbName;

    if(args.__ow_path && args.__ow_path.length) {
        let parts = args.__ow_path.split('/');
        //Verifies that a URL path with the name /books is present.
        if(parts.length === 3 && parts[1].toLowerCase() === 'books') {
            bookId = decodeURIComponent(parts[2]);
            //Verifies that the method used for the invocation is a PUT HTTP method.
            if (bookId && bookId != "" && args.__ow_method == "put") {
                  return dbUtils.addBook(bookId, dbUrl, dbName)
                     .then(function(result) {
                          return {
                              headers: {
                                  'Access-Control-Allow-Origin':'*',
                                  'Content-Type':'application/json'
                              },
                              statusCode:200,
                              body: new Buffer(JSON.stringify(result)).toString('base64')
                          }
                     }).catch(function(result) {
                           result = {"message": "An error occurred while retrieving the book with id " + book};
                           return {
                               headers: {
                                   'Access-Control-Allow-Origin':'*',
                                   'Content-Type':'application/json'
                               },
                               statusCode:500,
                               body: new Buffer(JSON.stringify(result)).toString('base64')
                           }
                     });
            } else {
                var result = {"message":"The requested resource was not found. Make sure you have specified an isbn and is using the correct method"};
                return {
                   headers: {
                     'Access-Control-Allow-Origin':'*',
                     'Content-Type':'application/json'
                   },
                   statusCode:404,
                   body: new Buffer(JSON.stringify(result)).toString('base64')
                }
            }
        } else {
           var result = {"result" : "The requested resource was not found"};
           return {
              headers: {
                 'Access-Control-Allow-Origin':'*',
                  'Content-Type':'application/json'
              },
              statusCode:404,
             body: new Buffer(JSON.stringify(result)).toString('base64')
           }
        }
    }
}

//Validates the parameters passed into the action.
function validateArgs(message) {
console.log("Validating arguments");
  if(!message) {
    console.error('No message argument!');
    return 'Internal error.  A message parameter was not supplied. Please contact your administrator.';
  }

  if (typeof message.dbUrl ==='undefined' || message.dbUrl == '') {
          return 'Couch Database URL is missing. Please make sure it is specified in the file installLibraryActions.sh.';
  }


  if (typeof message.dbName ==='undefined' || message.dbName == '') {
           return 'Library database name is missing. Please make sure it is specified in the file installLibraryActions.sh.';
  }
  return '';
}

module.exports.main = main;