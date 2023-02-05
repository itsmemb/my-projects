<?php 

include 'data.inc.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
  $title = $_POST["title"];
  $description = $_POST["description"];
  $genre = $_POST["genre"];
  $subject = $_POST["subject"];
  $medium = $_POST["medium"];
  $year = $_POST["year"];
  $museum = $_POST["museum"];
  
}
?>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="utf-8">
    <title>Chapter 12</title>
    
    <link rel="stylesheet" href="css/reset.css" />
    <link rel="stylesheet" href="css/styles.css" />
</head>
<body>
<?php include 'header.inc.php'; ?>
    
<main>
    <section class="results">
    <?php if ($_SERVER["REQUEST_METHOD"] == "POST") { ?>
    
    <table>
      <caption class="results__caption">Art Work Saved</caption>
      <tr>
        <td class="results__label">Title</td>
        <td class="results__value"><?php echo $title; ?></td>
      </tr>
      <tr>
        <td class="results__label">Description</td>
        <td class="results__value"><?php echo $description; ?></td>
      </tr>
      <tr>
        <td class="results__label">Genre</td>
        <td class="results__value"><?php echo $genre; ?></td>
      </tr>
      <tr>
        <td class="results__label">Subject</td>
        <td class="results__value"><?php echo $subject; ?></td>
      </tr>
      <tr>
        <td class="results__label">Medium</td>
        <td class="results__value"><?php echo $medium; ?></td>
      </tr>
      <tr>
        <td class="results__label">Year</td>
        <td class="results__value"><?php echo $year; ?></td>
      </tr>  
      <tr>
        <td class="results__label">Museum</td>
        <td class="results__value"><?php echo $museum; ?></td>
      </tr>
    </table>
    
    <?php 
    } 
    else {
        echo 'No form data ... <a href="chapter12-project1.php">go to form </a> and enter data';
    }
    ?>
    </section>
</main>       
</body>
</html>
