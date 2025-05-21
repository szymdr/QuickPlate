import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaGoogle } from "react-icons/fa";
import Navbar from "./components/Navbar";
import styles from "./Login.module.css";

export default function LoginPage() {
  useEffect(() => {
    document.title = "Log In ∙ QuickPlate";
  }, []);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) return alert("Nieudane logowanie");
    const { token } = await res.json();
    localStorage.setItem("token", token);
    // Check if the user is an admin
    const userRes = await fetch("http://localhost:8080/api/users/me", {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    const userData = await userRes.json();
    localStorage.setItem("user", JSON.stringify(userData));
    if (userData.role === "admin") {
      navigate("/users");
    }
    else {
      navigate("/");
    }
  };

  return (
    <div className={styles.loginPageContainer}>
    < Navbar />
    <div className={styles.loginPage}>
      <div className={styles.loginContainer}>
        <div className={styles.loginBox}>
          <div className="text-center mb-6">
            <h2>Log In</h2>
            <p>Please sign in to your existing account</p>
          </div>
          <div className={styles.inputGroup}>
            <label>Email</label>
            <input 
              type="email" 
              placeholder="example@gmail.com" 
              value={email} 
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className={styles.inputGroup}>
            <label>Password</label>
            <input 
              type="password" 
              placeholder="********" 
              value={password} 
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <div className={styles.rememberForgot}>
            <div>
              <input type="checkbox" id="remember" />
              <label htmlFor="remember">Remember me</label>
            </div>
            <a href="#">Forgot Password</a>
          </div>
          <button onClick={handleLogin} className={styles.loginButton}>
            Log In
          </button>
          <div className={styles.divider}>Or</div>
          <button className={styles.googleButton}>
            <FaGoogle className="mr-1" /> Sign in with Google
          </button>
          <div className={styles.signUp}>
            Don't have an account? <a href="/register">Sign Up</a>
          </div>
        </div>
      </div>
    </div>
    </div>
  );
}