import React from "react";
import { Link, useNavigate } from "react-router-dom";
import styles from "./Navbar.module.css";

export default function Navbar() {
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.reload();
  };

  return (
    <nav className={styles.navbar}>
      <div className={styles.logo}>
        <Link to="/">QuickPlate</Link>
      </div>
      <ul className={styles.links}>
        {token ? (
          <>
            <li>
              <Link to="/profile">Profile</Link>
            </li>
            <li>
              <button onClick={handleLogout} className={styles.logoutBtn}>
                Log Out
              </button>
            </li>
          </>
        ) : (            
            window.location.pathname !== "/login" &&
            window.location.pathname !== "/register" && (
            <>
                <li>
                    <Link to="/login">Log In</Link>
                </li>
                <li>
                    <Link to="/register">Register</Link>
                </li>
            </>
            )
          
        )}
      </ul>
    </nav>
  );
}