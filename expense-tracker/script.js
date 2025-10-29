const nameInput = document.getElementById("name");
const amountInput = document.getElementById("amount");
const categoryInput = document.getElementById("category");
const dateInput = document.getElementById("date");
const addBtn = document.getElementById("addBtn");
const list = document.getElementById("list");
const totalDisplay = document.getElementById("total");
const countDisplay = document.getElementById("count");
const monthSelect = document.getElementById("month");
const yearSelect = document.getElementById("year");

const months = ["January","February","March","April","May","June","July","August","September","October","November","December"];
months.forEach((m,i)=>{
  const opt=document.createElement("option");
  opt.value=i; opt.textContent=m;
  monthSelect.appendChild(opt);});
  
const thisYear=new Date().getFullYear();
for(let y=thisYear-2;y<=thisYear+2;y++){
  const opt=document.createElement("option");
  opt.value=y; opt.textContent=y;
  yearSelect.appendChild(opt);
}
monthSelect.value=new Date().getMonth();
yearSelect.value=thisYear;

let expenses = JSON.parse(localStorage.getItem("expenses")||"[]");
render();

addBtn.addEventListener("click", () => {
  const name = nameInput.value.trim();
  const amt = parseFloat(amountInput.value);
  const cat = categoryInput.value;
  const date = dateInput.value;

  if(!name || isNaN(amt) || amt <= 0 || !cat || !date){
    alert("Please fill all fields correctly!");
    return;
  }

  const newExpense = { id: Date.now(), name, amt, cat, date };
  expenses.push(newExpense);
  localStorage.setItem("expenses", JSON.stringify(expenses));

  clearInputs();
  render();
});

function formatDate(iso){
  const d = new Date(iso);
  if (isNaN(d)) return iso; 
  const dd = String(d.getDate()).padStart(2, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const yyyy = d.getFullYear();
  return `${dd}-${mm}-${yyyy}`;
}

function render(){
  list.innerHTML="";
  let total=0;
  const m=+monthSelect.value, y=+yearSelect.value;
  const filtered=expenses.filter(e=>{
    const d=new Date(e.date);
    return d.getMonth()===m && d.getFullYear()===y;
  });
  filtered.forEach(e=>{
    total+=e.amt;
    const li=document.createElement("li");
    li.innerHTML=`
      <span>
        <div>
          <strong>${e.name}</strong>
          <span class="category-badge">${e.cat}</span>
        </div>
        <small>${formatDate(e.date)}</small>
      </span>
      <div style="display:flex;align-items:center;gap:8px">
        <span>$${e.amt.toFixed(2)}</span>
        <button onclick="deleteExpense(${e.id})">Delete</button>
      </div>`;
    list.appendChild(li);
  });
  totalDisplay.textContent="$"+total.toFixed(2);
}

function deleteExpense(id){
  expenses = expenses.filter(e=>e.id!==id);
  localStorage.setItem("expenses", JSON.stringify(expenses));
  render();
}

function clearInputs(){
  nameInput.value="";
  amountInput.value="";
  categoryInput.value="";
  dateInput.value="";
}

monthSelect.onchange = yearSelect.onchange = render;
