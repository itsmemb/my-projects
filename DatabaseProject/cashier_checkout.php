<!DOCTYPE html>
<html>
 <head>
  <title>checkout.php</title>
 </head>
 <body>

<H1>Book checkout</H1>

 <?php

	$bookNumi = "Unknown";
  $bookTitlei = "Unknown";
  $numBoughti = "Unknown";
	if (isset($_GET["bookNum"])) { $bookNumi = $_GET["bookNum"]; }
  if (isset($_GET["bookTitle"])) { $bookTitlei = $_GET["bookTitle"]; }
  if (isset($_GET["numBought"])) { $numBoughti = $_GET["numBought"]; }

	// Validate bookNum, bookTitle, and numBought.
	$bookNum = test_input($bookNumi);
  $bookTitle = test_input($bookTitlei);
  $numBought = test_input($numBoughti);

	// Go get the User name and password for the MySQL access.
	$user_pw = getUser();

  // Output what user wants to do.
	echo "<h2>You wanted to buy " . $numBought . " copies of <i>" . $bookTitle . "</i></h2>";
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

  $sql1 = "SELECT * FROM bookstore.books WHERE isbn = " . $bookNum . " AND title = '" . $bookTitle . "'";
	//echo "Attempting query: <b>" . $sql1 . "</b><br>";

  if ($result = mysqli_query($conn, $sql1)) { // do query
    $row = mysqli_fetch_row($result);
    if ($row[2] == null) {  // check inputs
      echo "The given book title and isbn do not match. Check input and try again.";
    }
    else {
      $inStockCheck = $row[2];

      mysqli_free_result($result);

      if ($numBought == 0) {  // if user attempts to buy 0
        echo "<br>Please enter more than 0 books to purchase.";

        if ($inStockCheck == 0) { // if there are 0 in stock
          echo "<br>Sorry, <i>" . $bookTitle . "</i> is not in stock.";
        }
      } // end if for user buying 0

      elseif ($inStockCheck > 0 && $inStockCheck >= $numBought) { // if there are enough books in stock
        echo "There are " . $inStockCheck ." copies of <i>" . $bookTitle . "</i> in stock.<br><br>";

	      // Create a string for query. Identify our bookstore schema. Then execute the query.
	      $sql2 = "CALL bookstore.checkout(" . $bookNum . ", '" . $bookTitle . "', " . $numBought . ", " . $inStockCheck .")";

	      mysqli_select_db($conn, "bookstore");

	        if ($result = mysqli_query($conn, $sql2)) {
             while ($row = mysqli_fetch_row($result)) {
               printf ("<u>Book bought</u> <br> Isbn: %s <br> Title: %s <br> Quantity purchased: %s <br> Price each: %s <br> Total price: %s.",
                      $row[0], $row[1], $row[2], $row[3], number_format((float)$row[2] * $row[3], 2, '.', ''));
             }
		         mysqli_free_result($result); // Free result set
	        }
      }  // end if for enough books in stock

      elseif ($inStockCheck > 0 AND $inStockCheck < $numBought) { // if there are not enough in stock
        echo  "Sorry, there are not enough copies of <i>" . $bookTitle ."</i> in stock. We have " . $inStockCheck . " copies.";
      }
      else {  // if there are none in stock
        echo "<br>Sorry, <i>" . $bookTitle . "</i> is not in stock.";
      }
    } // end else for search is in database
  } // end do query if

	// Close the connection to our datatbase server
	mysqli_close($conn);

	// Let's validate our input data.
	// https://www.php.net/manual/en/ref.strings.php
	function test_input($data) {
		$data = trim($data);
		$data = stripslashes($data);
		$data = htmlspecialchars($data);
		return $data;
	}

	// Glom onto the user name and password for MySQL.
	function getUser() {

		$myfile = fopen("DB_USER.txt", "r") or die("Unable to open user file!");
		$file_input = fread($myfile, filesize("DB_USER.txt"));
		// https://www.php.net/manual/en/function.explode.php
		$user_pw = explode(" ", $file_input);
		// echo "<p>From DB_USER.txt: User name = " . $user_pw[0] . ", Password  = " . $user_pw[1];
		fclose($myfile);
		return $user_pw;
	}
 ?>

 <form action="cashier_fill_checkout.html" method="get">
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
