import React from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { FaArrowLeft } from "react-icons/fa";
import styles from "./Navbar.module.css";

export default function Navbar() {
  const token = localStorage.getItem("token");
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.reload();
  };

  return (
    <nav className={styles.navbar}>
      {location.pathname !== "/" && (
        <button
          className={styles.backBtn}
          onClick={() => navigate(-1)}
          aria-label="Go back"
        >
          <FaArrowLeft />
        </button>
      )}
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
          location.pathname !== "/login" &&
          location.pathname !== "/register" && (
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