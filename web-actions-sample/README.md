# Web Action Sample

## Web Actions Installation Actions.

Follow the blog to understand and use this sample.

This sample consists of the code needed for the installation of the library package.

DB_URL=http://172.17.0.1:5984
DB_NAME=library-database

wsk package update library \
-a description "This package manages the library API backend actions" \
-p dbUrl "$DB_URL" \
-p dbName "$DB_NAME" -i

zip -j actionFiles/getBooks/getBooksAction.zip actionFiles/getBooks/getBooksAction.js actionFiles/common/dbUtils.js actionFiles/getBooks/package.json
wsk action update library/getBooksAction actionFiles/getBooks/getBooksAction.zip --web true -i --kind nodejs:default

zip -j actionFiles/putBooks/putBooksAction.zip actionFiles/putBooks/putBooksAction.js actionFiles/common/dbUtils.js actionFiles/putBooks/package.json
wsk action update library/putBooksAction actionFiles/putBooks/putBooksAction.zip --web true -i --kind nodejs:default

zip -j actionFiles/deleteBooks/deleteBooksAction.zip actionFiles/deleteBooks/deleteBooksAction.js actionFiles/common/dbUtils.js actionFiles/deleteBooks/package.json
wsk action update library/deleteBooksAction actionFiles/deleteBooks/deleteBooksAction.zip --web true -i --kind nodejs:default
