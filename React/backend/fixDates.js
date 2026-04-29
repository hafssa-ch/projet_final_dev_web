
const db = require('./database');
db.all(`SELECT id, date FROM expenses`, [], (err, rows) => {
  if (err) return console.error(err);
  rows.forEach(row => {
    let newDate = row.date;
    if (row.date.match(/^\d{4}-\d{2}-\d{2}$/)) return; // déjà bon
    // si format jj/mm/aaaa
    if (row.date.match(/^\d{2}\/\d{2}\/\d{4}$/)) {
      newDate = row.date.substr(6,4) + '-' + row.date.substr(3,2) + '-' + row.date.substr(0,2);
    }
    // si format mm/dd/aaaa
    else if (row.date.match(/^\d{2}\/\d{2}\/\d{4}$/)) {
      newDate = row.date.substr(6,4) + '-' + row.date.substr(0,2) + '-' + row.date.substr(3,2);
    }
    if (newDate !== row.date) {
      db.run(`UPDATE expenses SET date = ? WHERE id = ?`, [newDate, row.id]);
      console.log(`Mise à jour: ${row.date} -> ${newDate}`);
    }
  });
  console.log('Terminé');
  db.close();
});