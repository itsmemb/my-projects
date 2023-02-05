<!DOCTYPE html>
<html>
 <head>
  <title>title.php</title>
 </head>
 <body>

<H1>Book search by title</H1>

 <?php
	// If we got names, use them.
	$titlei = "Unknown";
	if (isset($_GET["title"])) { $titlei = $_GET["title"]; }

	// Validate
	$title = test_input($titlei);

	// Go get the User name and password for the MySQL access.
	$user_pw = getUser();

	echo "<h2>You searched for a book by the title '" . $title . "'</h2>";
?>

<?php

	// Create a connection to the database server.
	$dbhost = "localhost:3306";
	$dbuser = $user_pw[0];
	$dbpass = $user_pw[1];
	$conn = mysqli_connect($dbhost, $dbuser, $dbpass);
	if(! $conn )
	{
		echo "Error: Unable to connect to MySQL." . "<br>\n";
		echo "Debugging errno: " . mysqli_connect_errno() . "<br>\n";
		echo "Debugging error: " . mysqli_connect_error() . "<br>\n";
		die("Could not connect: " . mysqli_error());
	}

	// Create a string representing our query. Identify our bookstore schema. Then execute the query and get back our result set.
	$sql = "SELECT isbn, title, price, quantity " .
         "FROM bookstore.books " .
	       "WHERE title = '" . $title . "'";
	//echo "Attempting query:<h4> " . $sql . "</h4>";

	mysqli_select_db($conn, "bookstore");
	if ($result = mysqli_query($conn, $sql))
	{
		while ($row = mysqli_fetch_row($result))
    {
      printf ("<u>Book found</u> <br> Isbn: %s <br> Title: %s <br> Price each: %s <br> Quantity in stock: %s.",
   		         $row[0], $row[1], $row[2], $row[3]);
		}
		mysqli_free_result($result); // Free result set
	}

	mysqli_close($conn); // Close the connection to our datatbase server

	// Let's validate our input data.
	// https://www.php.net/manual/en/ref.strings.php
	function test_input($data) {
		$data = trim($data);
		$data = stripslashes($data);
		$data = htmlspecialchars($data);
		return $data;
	}

	// Glom onto the user name and password for MySQL.
	function getUser()
	{
		$myfile = fopen("DB_USER.txt", "r") or die("Unable to open user file!");
		$file_input = fread($myfile, filesize("DB_USER.txt"));
		// https://www.php.net/manual/en/function.explode.php
		$user_pw = explode(" ", $file_input);
		// echo "<p>From DB_USER.txt: User name = " . $user_pw[0] . ", Password  = " . $user_pw[1];
		fclose($myfile);
		return $user_pw;
	}
 ?>

 <form action="cashier_title_search.html" method="get">
   <p style="font-size:200%;">
     <input type="submit" value="Back">
   </p>
 </form>

 <form action="cashier_view.html" method="get">
   <p style="font-size:200%;">
     <input type="submit" value="Cashier Home">
   </p>
 </form>

 </body>
</html>
