<?php
$servername = "localhost";
$username   = "emilio";
$password   = "StrongPass123!";
$database   = "smart_assignment_planner";

$conn = new mysqli($servername, $username, $password, $database);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$filter = $_GET['filter'] ?? 'all';
$course = $_GET['course'] ?? '';

$baseQuery = "SELECT A.name, A.due_date, A.completion_status, C.course_name
              FROM Assignment A
              JOIN Course C ON A.course_id = C.course_id";

$where = [];
if (!empty($course)) {
    $where[] = "C.course_name = '".$conn->real_escape_string($course)."'";
}

switch ($filter) {
    case 'completed':
        $where[] = "A.completion_status = TRUE";
        break;

    case 'incomplete':
        $where[] = "A.completion_status = FALSE";
        break;
}

if (!empty($where)) {
    $sql = $baseQuery . " WHERE " . implode(" AND ", $where) . " ORDER BY A.due_date ASC";
} else {
    $sql = $baseQuery . " ORDER BY A.due_date ASC";
}

$result = $conn->query($sql);
$courses = $conn->query("SELECT DISTINCT course_name FROM Course ORDER BY course_name ASC");
?>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Smart Assignment Planner</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 40px; background: #f7f9fc; }
    h1 { color: #333; }
    .buttons { margin-bottom: 20px; }
    button, select {
      background: #007bff; color: white; border: none; padding: 10px 15px;
      margin-right: 10px; border-radius: 6px; cursor: pointer; font-size: 14px;
    }
    button:hover, select:hover { background: #0056b3; }
    table { border-collapse: collapse; width: 80%; background: white; box-shadow: 0 0 8px #ccc; }
    th, td { padding: 10px 12px; border: 1px solid #ddd; }
    th { background: #007bff; color: white; }
    tr:nth-child(even) { background: #f2f2f2; }
    .filter-label { font-weight: bold; color: #555; margin-right: 10px; }
  </style>
</head>
<body>
<h1>Assignments Overview</h1>

<div class="buttons">
  <!-- Show All -->
  <form method="GET" style="display:inline;">
    <input type="hidden" name="course" value="<?= htmlspecialchars($course) ?>">
    <button type="submit" name="filter" value="all">Show All</button>
  </form>

  <!-- Completed -->
  <form method="GET" style="display:inline;">
    <input type="hidden" name="course" value="<?= htmlspecialchars($course) ?>">
    <button type="submit" name="filter" value="completed" style="background: #28a745;">Completed</button>
  </form>

  <!-- Incomplete -->
  <form method="GET" style="display:inline;">
    <input type="hidden" name="course" value="<?= htmlspecialchars($course) ?>">
    <button type="submit" name="filter" value="incomplete" style="background: #dc3545;">Incomplete</button>
  </form>

  <!-- Dropdown -->
  <form method="GET" style="display:inline;">
    <label class="filter-label" for="course">By Course:</label>
    <select name="course" onchange="this.form.submit()">
      <option value="">Select Course</option>
      <?php while($rowC = $courses->fetch_assoc()): ?>
        <option value="<?= htmlspecialchars($rowC['course_name']) ?>"
          <?= $rowC['course_name'] == $course ? 'selected' : '' ?>>
          <?= htmlspecialchars($rowC['course_name']) ?>
        </option>
      <?php endwhile; ?>
    </select>
    <input type="hidden" name="filter" value="<?= htmlspecialchars($filter) ?>">
  </form>
</div>

<table>
  <tr>
    <th>Assignment</th>
    <th>Course</th>
    <th>Due Date</th>
    <th>Completed?</th>
  </tr>
  <?php if ($result && $result->num_rows > 0): ?>
      <?php while($row = $result->fetch_assoc()): ?>
        <tr>
          <td><?= htmlspecialchars($row["name"]) ?></td>
          <td><?= htmlspecialchars($row["course_name"]) ?></td>
          <td><?= htmlspecialchars($row["due_date"]) ?></td>
          <td><?= $row["completion_status"] ? "Yes" : "No" ?></td>
        </tr>
      <?php endwhile; ?>
  <?php else: ?>
      <tr><td colspan="4">No data found.</td></tr>
  <?php endif; ?>
</table>
</body>
</html>
<?php $conn->close(); ?>

