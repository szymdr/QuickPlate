import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
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
  const location = useLocation();
  const redirectTo = location.state?.redirectTo;
  const redirectState = {
    cart:      location.state?.cart,
    dateTime:  location.state?.dateTime,
    guests:    location.state?.guests
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) return alert("Nieudane logowanie");
    const data = await res.json();              // { token: "…" }
    localStorage.setItem('token', data.token);

    const meRes = await fetch('http://localhost:8080/api/users/me', {
      headers: {
        'Authorization': `Bearer ${data.token}`
      }
    });
    if (!meRes.ok) {
      const err = await meRes.text();
      return alert('Nie udało się pobrać profilu: ' + err);
    }
    const me = await meRes.json();               // { id, firstName, … }
    localStorage.setItem('user', JSON.stringify(me));

    if (redirectTo) {
      return navigate(redirectTo, { replace: true, state: redirectState });
    }

    if (me.accountType.name === 'RESTAURANT_OWNER') {
      navigate('/owner', { replace: true });
    } else {
      navigate('/', { replace: true });
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