document.getElementById("addEmployeeForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const name = document.getElementById("name").value;
    const age = document.getElementById("age").value;
    const position = document.getElementById("position").value;
    const tr = document.createElement("tr");
    tr.innerHTML = `
    <td>${name}</td>
    <td>${age}</td>
    <td>${position}</td>
    `;
    document.getElementById("employeeList").appendChild(tr);

    document.getElementById("addEmployeeForm").reset();
});