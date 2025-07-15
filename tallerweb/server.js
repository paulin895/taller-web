const express = require('express');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const cors = require('cors');
const mysql = require('mysql2');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

// Configuración de la base de datos
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '', // cambiá si tu usuario MySQL tiene contraseña
    database: 'taller'
});

db.connect(err => {
    if (err) {
        console.error('Error al conectar la base de datos:', err);
    } else {
        console.log('Conectado a la base de datos MySQL.');
    }
});

// Ruta de login
app.post('/procesar-login', (req, res) => {
    const { usuario, contrasena } = req.body;

    if (!usuario || !contrasena) {
        return res.status(400).json({ mensaje: 'Faltan datos' });
    }

    db.query('SELECT * FROM usuarios WHERE usuario = ?', [usuario], (err, resultados) => {
        if (err) return res.status(500).json({ mensaje: 'Error de servidor' });

        if (resultados.length === 0) {
            return res.status(401).json({ mensaje: 'Usuario no encontrado' });
        }

        const usuarioDB = resultados[0];

        bcrypt.compare(contrasena, usuarioDB.contrasena, (err, resultado) => {
            if (resultado) {
                const token = jwt.sign({ id: usuarioDB.id, usuario: usuarioDB.usuario }, 'clave_secreta', { expiresIn: '2h' });
                res.json({ token });
            } else {
                res.status(401).json({ mensaje: 'Contraseña incorrecta' });
            }
        });
    });
});

app.listen(3000, () => {
    console.log('Servidor corriendo en http://localhost:3000');
});