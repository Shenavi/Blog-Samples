const request = require('request');

// This method retrieves the book which corresponds to the mentioned isbn/book id
function getBook(isbn, dbUrl, dbName) {
    console.log("IN the get books function");
    var options = {
        followAllRedirects: true,
        url: dbUrl + '/' + dbName + "/" + isbn,
        headers: {
            'Accept': 'application/json'
        },
        rejectUnauthorized: false,
    };

    console.log(JSON.stringify(options));
    return new Promise((resolve, reject) => {
        request.get(options, function (err, response, body) {
            if (!err && response.statusCode === 200) {
                resolve({ message: "Successfully retrieved the document", doc: response.body });
            } else if (response.statusCode === 404) {
                resolve({ message: "No record found for the book " + isbn, doc: null });
            } else {
                console.info("Error occurred while getting the document");
                reject({ message: "Error occurred while getting the book", doc: null })
            }
        });
    });
};

// This method retrieves all the books in the database.
function getAllBooks(dbUrl, dbName) {
    console.log("IN the get books function");
    var options = {
        followAllRedirects: true,
        url: dbUrl + '/' + dbName + "/_all_docs/",
        headers: {
            'Accept': 'application/json'
        },
        rejectUnauthorized: false,
    };

    console.log(JSON.stringify(options));
    return new Promise((resolve, reject) => {
        request.get(options, function (err, response, body) {
            if (!err && response.statusCode === 200) {
                resolve({ message: "Successfully retrieved the document", doc: response.body });
            } else if (response.statusCode === 404) {
                resolve({ message: "No records found", doc: null });
            } else {
                console.info("Error occurred while getting the document");
                reject({ message: "Error occurred while getting all the books", doc: null })
            }
        });
    });
};

// This method add a book with the mentioned isbn/book id
function addBook(isbn, dbUrl, dbName) {
    console.log("In the add book method");
    var data = {};
    data.description = "This is a sample book";
    data.name = isbn;

    var options = {
        followAllRedirects: true,
        url: dbUrl + "/" + dbName + "/" + isbn,
        headers: {
            'Accept': 'application/json'
        },
        rejectUnauthorized: false,
        json: data
    };

    console.log(JSON.stringify(options));
    return new Promise((resolve, reject) => {
        request.put(options, function (err, response, body) {
            if (!err && response.statusCode === 201) {
                resolve({ message: "The book " + isbn + " was successfully added" });
            }
            else {
                reject({ message: "Error occurred while adding the details" })
            }
        });
    });
}

// This method handles the deletion of a book with the specified isbn/book id
function deleteBook(isbn, dbUrl, dbName) {

 return getRevId(isbn, dbUrl, dbName).then(function(revId) {
    return Promise.resolve(deleteBookByIsbn(isbn,revId, dbUrl, dbName));
    }).catch(function(reason) {
             var rejmsg = reason.toString();
             console.error(rejmsg);
             return Promise.reject(rejmsg);
    });
}

// This method deletes the book which corresponds to the mentioned isbn/book id
function deleteBookByIsbn(isbn,revId, dbUrl, dbName) {
    console.log("IN the delete books function");
    var options = {
        followAllRedirects: true,
        url: dbUrl + '/' + dbName + "/" + isbn + "?rev=" + revId,
        headers: {
            'Accept': 'application/json'
        },
        rejectUnauthorized: false,
    };

    console.log(JSON.stringify(options));
    return new Promise((resolve, reject) => {
        request.delete(options, function (err, response, body) {
            if (!err && response.statusCode === 200) {
                resolve({ message: "Successfully deleted the book " + isbn });
            } else if (response.statusCode === 404) {
                resolve({ message: "No record found, hence unable to delete the book " + isbn });
            } else {
                console.info("Error occurred while getting the document");
                reject({ message: "Error occurred while deleting the book", doc: null })
            }
        });
    });
};

//This method returns the rev-id of the book which is needed for the deletion.
function getRevId(isbn, dbUrl, dbName) {
 return getBook(isbn, dbUrl, dbName).then(function(booksObject) {
    var books = JSON.parse(booksObject.doc);
        return Promise.resolve(books._rev);
    }).catch(function(reason) {
        var rejmsg = reason.toString();
        console.error(rejmsg);
        return Promise.reject(rejmsg);
    });
};

module.exports.getBook = getBook;
module.exports.getAllBooks = getAllBooks;
module.exports.addBook = addBook;
module.exports.deleteBook = deleteBook;
