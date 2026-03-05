const { Sequelize } = require('sequelize');
const config = require('../config/database').development;

const sequelize = new Sequelize(
    config.database,
    config.username,
    config.password,
    {
        host: config.host,
        dialect: config.dialect,
        logging: config.logging,
    }
);

const db = {};
db.Sequelize = Sequelize;
db.sequelize = sequelize;

module.exports = db;
