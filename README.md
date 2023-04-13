Программа для поиска и оценки книг, что-то похожее на кинопоиск, только для книг

DB_scheme.png


## Code from diagram:
Users 
-
 id_user PK int 
  name string
  email string 
  login varchar 
  birthday timestamp


Books
-
 id_book PK int 
 name varchar
 description varchar
 release_date timestamp
 volume int
 rate int
  

  
Books_genres
-
  id_book int FK >- Books.id_book
  id_genre int FK >- Genres.id_genre

Genres 
-
  id_genre PK int 
  name_genre string


Likes
-
  id_user int FK >- Users.id_user
  id_book int FK >- Books.id_book
  


Users_friend
-
  id_user_one int FK >- Users.id_user
  id_user_two int FK >- Users.id_user


Book_author
-
  id_book int FK >- Books.id_book
  id_author int FK >- Authors.id_author


Authors
-
  id_author PK int
  name varchar