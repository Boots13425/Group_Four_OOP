const express = require('express');
const app = express();

app.get('/', (req, res) => {
  // Query the database and render the results
  connection.query('SELECT * FROM bellamy', (err, results) => {
    if (err) {
      console.error('Error querying the database:', err);
      res.status(500).send('Error querying the database');
    } else {
      res.render('index', { results });
    }
  });
});

app.listen(3000, () => {
  console.log('Server listening on port 3000');
});