<?php
$servername = "localhost";
$username   = "phpuser";
$password   = "StrongPass123";
$database   = "smart_assignment_planner";

$conn = new mysqli($servername, $username, $password, $database);
if ($conn->connect_error) { die("Connection failed: " . $conn->connect_error); }

$filter  = $_GET['filter'] ?? 'all';
$course  = $_GET['course'] ?? '';
$student = $_GET['student'] ?? '';
$queryMode = $_GET['query'] ?? '';

$baseQuery = "SELECT A.assignment_id, A.name, A.due_date, A.completion_status, C.course_name
              FROM Assignment A
              JOIN Course C ON A.course_id = C.course_id";

$where = [];

// Student filter → only assignments in courses the student takes
if (!empty($student)) {
    $where[] = "A.course_id IN (
        SELECT course_id FROM Takes WHERE email = '".$conn->real_escape_string($student)."'
    )";
}

// Course filter
if (!empty($course)) {
    $where[] = "C.course_name = '".$conn->real_escape_string($course)."'";
}

// Status filter
switch ($filter) {
    case 'completed':   $where[] = "A.completion_status = 1"; break;
    case 'incomplete':  $where[] = "A.completion_status = 0"; break;
}

if (!empty($where))
    $sql = $baseQuery ." WHERE ". implode(" AND ", $where) ." ORDER BY A.due_date ASC";
else
    $sql = $baseQuery ." ORDER BY A.due_date ASC";

// Load dropdown data
$courses  = $conn->query("SELECT DISTINCT course_name FROM Course ORDER BY course_name ASC");
$students = $conn->query("SELECT email, name FROM `User` ORDER BY name ASC");

/* =====================================================
   SPECIAL QUERY BUTTONS
===================================================== */
function runSpecialQuery($conn, $queryMode)
{
    switch ($queryMode) {

        case "summary":
            return $conn->query("
                SELECT C.course_name,
                       SUM(A.completion_status = 1) AS completed_count,
                       SUM(A.completion_status = 0) AS incomplete_count
                FROM Assignment A
                JOIN Course C ON A.course_id = C.course_id
                GROUP BY C.course_id
                ORDER BY completed_count DESC;
            ");

        case "overdue":
            return $conn->query("
                SELECT A.name, C.course_name, A.due_date
                FROM Assignment A
                JOIN Course C ON A.course_id = C.course_id
                WHERE A.due_date < CURDATE()
                ORDER BY A.due_date;
            ");

        case "students":
            return $conn->query("
                SELECT U.name, U.email, COUNT(T.course_id) AS num_courses
                FROM User U
                LEFT JOIN Takes T ON U.email = T.email
                GROUP BY U.email
                ORDER BY num_courses DESC;
            ");

        case "types":
            return $conn->query("
                SELECT type, COUNT(*) AS total
                FROM Assignment
                GROUP BY type
                ORDER BY total DESC;
            ");

        default:
            return false;
    }
}

$specialResult = ($queryMode !== '') ? runSpecialQuery($conn, $queryMode) : null;
$mainResult = $conn->query($sql);
?>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Smart Assignment Planner</title>
<style>
    body { font-family: Arial,sans-serif; margin: 30px; background:#eef2f5; }
    h1 { color:#333; }

    button, select, input[type=text], input[type=date] {
        padding:8px; border-radius:5px; border:1px solid #aaa;
    }
    button { background:#007bff; color:white; cursor:pointer; }
    button:hover { background:#0056b3; }

    .section { margin:20px 0; padding:15px; background:white; border-radius:8px; box-shadow:0 0 5px #ccc; }
    .section h3 { cursor:pointer; margin-top:0; }

    table { border-collapse:collapse; width:90%; background:white; margin-top:20px;
            box-shadow:0 0 8px #ccc; }
    th,td { padding:10px; border:1px solid #ddd; }
    th { background:#007bff; color:white; }
    tr:nth-child(even) { background:#f2f2f2; }

    .msg { background:#d4edda; padding:10px; border-radius:5px; color:#155724; margin-bottom:10px; }
</style>

<script>
function toggle(id){
    var el=document.getElementById(id);
    el.style.display=(el.style.display==="none")?"block":"none";
}
</script>

</head>
<body>

<h1>Smart Assignment Planner</h1>

<?php if(isset($_GET['msg'])): ?>
<div class="msg"><?= htmlspecialchars($_GET['msg']) ?></div>
<?php endif; ?>

<!-- =====================================================
     FILTERS
===================================================== -->
<div class="section">
    <form method="GET" style="display:inline;">
        <input type="hidden" name="course" value="<?= htmlspecialchars($course) ?>">
        <input type="hidden" name="student" value="<?= htmlspecialchars($student) ?>">
        <button name="filter" value="all">Show All</button>
    </form>

    <form method="GET" style="display:inline;">
        <input type="hidden" name="course" value="<?= htmlspecialchars($course) ?>">
        <input type="hidden" name="student" value="<?= htmlspecialchars($student) ?>">
        <button name="filter" value="completed" style="background:#28a745;">Completed</button>
    </form>

    <form method="GET" style="display:inline;">
        <input type="hidden" name="course" value="<?= htmlspecialchars($course) ?>">
        <input type="hidden" name="student" value="<?= htmlspecialchars($student) ?>">
        <button name="filter" value="incomplete" style="background:#dc3545;">Incomplete</button>
    </form>

    <!-- Course dropdown -->
    <form method="GET" style="display:inline;">
        <label>By Course:</label>
        <select name="course" onchange="this.form.submit()">
            <option value="">All Courses</option>
            <?php while($rowC = $courses->fetch_assoc()): ?>
                <option value="<?= $rowC['course_name'] ?>"
                    <?= ($rowC['course_name']==$course?'selected':'') ?>>
                    <?= $rowC['course_name'] ?>
                </option>
            <?php endwhile; ?>
        </select>

        <!-- Student dropdown -->
        <label style="margin-left:10px;">By Student:</label>
        <select name="student" onchange="this.form.submit()">
            <option value="">All Students</option>
            <?php while($s = $students->fetch_assoc()): ?>
                <option value="<?= $s['email'] ?>"
                    <?= ($s['email']==$student?'selected':'') ?>>
                    <?= htmlspecialchars($s['name']) ?>
                </option>
            <?php endwhile; ?>
        </select>

        <input type="hidden" name="filter" value="<?= htmlspecialchars($filter) ?>">
    </form>
</div>

<!-- =====================================================
     ADVANCED QUERY BUTTONS
===================================================== -->
<div class="section">
    <h3>Advanced Queries</h3>
    <a href="index.php?query=summary"><button>Course Completion Summary</button></a>
    <a href="index.php?query=overdue"><button>Overdue Assignments</button></a>
    <a href="index.php?query=students"><button>Student Course Counts</button></a>
    <a href="index.php?query=types"><button>Assignment Type Summary</button></a>
</div>

<!-- =====================================================
     CRUD: ASSIGNMENTS
===================================================== -->
<div class="section">
    <h3 onclick="toggle('addA')">➕ Add Assignment</h3>
    <div id="addA" style="display:none;">
        <form action="crud/add_assignment.php" method="POST">
            <label>Course:</label>
            <select name="course_id" required>
                <?php $cs = $conn->query("SELECT course_id, course_name FROM Course ORDER BY course_name");
                while($c = $cs->fetch_assoc()): ?>
                    <option value="<?= $c['course_id'] ?>"><?= $c['course_name'] ?></option>
                <?php endwhile; ?>
            </select><br><br>

            <label>Name:</label>
            <input type="text" name="name" required><br><br>

            <label>Due Date:</label>
            <input type="date" name="due_date"><br><br>

            <label>Type:</label>
            <input type="text" name="type"><br><br>

            <label>Complete?</label>
            <input type="checkbox" name="completion_status"><br><br>

            <button type="submit">Add</button>
        </form>
    </div>
</div>

<div class="section">
    <h3 onclick="toggle('editA')">✏️ Edit Assignment</h3>
    <div id="editA" style="display:none;">
        <form action="crud/update_assignment.php" method="POST">
            <label>Assignment ID:</label>
            <input type="text" name="assignment_id" required><br><br>

            <label>Name:</label>
            <input type="text" name="name" required><br><br>

            <label>Due Date:</label>
            <input type="date" name="due_date"><br><br>

            <label>Status (0 or 1):</label>
            <input type="text" name="completion_status" required><br><br>

            <button type="submit">Update</button>
        </form>
    </div>
</div>

<div class="section">
    <h3 onclick="toggle('delA')">🗑️ Delete Assignment</h3>
    <div id="delA" style="display:none;">
        <form action="crud/delete_assignment.php" method="POST">
            <label>Assignment ID:</label>
            <input type="text" name="assignment_id" required><br><br>
            <button type="submit" style="background:#dc3545;">Delete</button>
        </form>
    </div>
</div>

<!-- =====================================================
     CRUD: USERS
===================================================== -->
<div class="section">
    <h3 onclick="toggle('addU')">➕ Add User</h3>
    <div id="addU" style="display:none;">
        <form action="crud/add_user.php" method="POST">
            <label>Email:</label>
            <input type="text" name="email" required><br><br>

            <label>Name:</label>
            <input type="text" name="name" required><br><br>

            <button type="submit">Add User</button>
        </form>
    </div>
</div>

<div class="section">
    <h3 onclick="toggle('editU')">✏️ Edit User</h3>
    <div id="editU" style="display:none;">
        <form action="crud/update_user.php" method="POST">
            <label>Email:</label>
            <input type="text" name="email" required><br><br>

            <label>New Name:</label>
            <input type="text" name="name" required><br><br>

            <button type="submit">Update User</button>
        </form>
    </div>
</div>

<div class="section">
    <h3 onclick="toggle('delU')">🗑️ Delete User</h3>
    <div id="delU" style="display:none;">
        <form action="crud/delete_user.php" method="POST">
            <label>Email:</label>
            <input type="text" name="email" required><br><br>

            <button type="submit" style="background:#dc3545;">Delete User</button>
        </form>
    </div>
</div>

<!-- =====================================================
     RESULTS OUTPUT TABLE
===================================================== -->

<h2>Results</h2>

<?php
$resultToShow = ($specialResult) ? $specialResult : $mainResult;

if ($resultToShow && $resultToShow->num_rows > 0):
?>
<table>
    <tr>
        <?php foreach($resultToShow->fetch_fields() as $field): ?>
            <th><?= $field->name ?></th>
        <?php endforeach; ?>
    </tr>

    <?php while($row = $resultToShow->fetch_assoc()): ?>
        <tr>
            <?php foreach($row as $col): ?>
                <td><?= htmlspecialchars($col) ?></td>
            <?php endforeach; ?>
        </tr>
    <?php endwhile; ?>
</table>

<?php else: ?>
<p>No results.</p>
<?php endif; ?>

</body>
</html>
<?php $conn->close(); ?>

