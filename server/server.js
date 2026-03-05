const express = require('express');
const cors = require('cors');
require('dotenv').config();

const { sequelize } = require('./models');
const routes = require('./routes');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

app.use('/api', routes);

app.get('/', (req, res) => {
    res.send('Smart Tourism API is running.');
});

// Sync db
sequelize.sync()
    .then(() => {
        console.log('Database connected & synced.');
        app.listen(PORT, () => {
            console.log(`Server is running on port ${PORT}.`);
        });
    })
    .catch((err) => {
        console.error('Failed to connect to database: ' + err.message);
    });
