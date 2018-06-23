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
        //Checks if the path /books is present in the request
        if(parts.length === 3 && parts[1].toLowerCase() === 'books') {
            bookId = decodeURIComponent(parts[2]);
            console.log("bookid " + bookId);
            //Verifies if the method that the web action was invoked with is a get HTTP method.
            //Verifies if a book id is present.
            if (bookId && bookId != "" && args.__ow_method == "get") {
                return dbUtils.getBook(bookId, dbUrl, dbName)
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
                        result = {"message": "An error occurred while retrieving the book with id " + bookId};
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
                var result = {"message":"The requested resource was not found YYY"};
                return {
                   headers: {
                     'Access-Control-Allow-Origin':'*',
                     'Content-Type':'application/json'
                   },
                   statusCode:404,
                   body: new Buffer(JSON.stringify(result)).toString('base64')
                }
            }
        } else if(parts.length === 2 && parts[1].toLowerCase() === 'books') {
            if (args.__ow_method == "get") {
                return dbUtils.getAllBooks(dbUrl, dbName)
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
                    result = {"message": "An error occurred while retrieving the book with id " + bookId};
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
               var result = {"message":"The requested resource was not found"};
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
           var result = {"result" : "The requested resource was not found XXX"};
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