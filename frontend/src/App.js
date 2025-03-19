import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";

function Home() {
    return (
        <div>
            <h1>Hello from Frontend!</h1>
            <Link to="/dashboard">Go to Dashboard</Link>
        </div>
    );
}

function Dashboard() {
    return <h2>Dashboard Page</h2>;
}

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/dashboard" element={<Dashboard />} />
            </Routes>
        </Router>
    );
}

export default App;