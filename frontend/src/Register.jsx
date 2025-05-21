import { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Register.module.css";

document.title = "Sign Up ∙ QuickPlate";

export default function RegisterPage() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName]   = useState("");
  const [email, setEmail]         = useState("");
  const [password, setPassword]   = useState("");
  const [role, setRole]           = useState("USER");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    const res = await fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ firstName, lastName, email, password, role }),
    });
    if (!res.ok) {
      const err = await res.text();
      return alert("Rejestracja nie powiodła się: " + err);
    }
    alert("Konto utworzone, zaloguj się");
    navigate("/login");
  };

  return (
    <div className={styles.registerPage}>
      <div className={styles.registerContainer}>
        <div className={styles.registerBox}>
          <h2>Create Account</h2>
          <p>Please fill in to register</p>

          <form onSubmit={handleRegister}>
            <div className={styles.inputGroup}>
              <label>First Name</label>
              <input
                type="text"
                placeholder="John"
                value={firstName}
                onChange={e => setFirstName(e.target.value)}
                required
              />
            </div>

            <div className={styles.inputGroup}>
              <label>Last Name</label>
              <input
                type="text"
                placeholder="Doe"
                value={lastName}
                onChange={e => setLastName(e.target.value)}
                required
              />
            </div>

            <div className={styles.inputGroup}>
              <label>Email</label>
              <input
                type="email"
                placeholder="example@gmail.com"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
              />
            </div>

            <div className={styles.inputGroup}>
              <label>Password</label>
              <input
                type="password"
                placeholder="********"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" className={styles.registerButton}>
              Sign Up
            </button>
          </form>

          <div className={styles.divider}>Or</div>
          <p>
            Already have an account?{" "}
            <a href="/login">Log In</a>
          </p>
        </div>
      </div>
    </div>
  );
}