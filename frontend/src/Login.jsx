import { useState } from "react";
import { FaGoogle } from "react-icons/fa";
//import "./Login.css";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="text-center mb-6">
          <h2>Log In</h2>
          <p>Please sign in to your existing account</p>
        </div>
        <div className="input-group">
          <label>Email</label>
          <input 
            type="email" 
            placeholder="example@gmail.com" 
            value={email} 
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div className="input-group">
          <label>Password</label>
          <input 
            type="password" 
            placeholder="********" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="remember-forgot">
          <div>
            <input type="checkbox" id="remember" />
            <label htmlFor="remember">Remember me</label>
          </div>
          <a href="#">Forgot Password</a>
        </div>
        <button className="login-button">Log In</button>
        <div className="divider">Or</div>
        <button className="google-button">
          <FaGoogle className="mr-2" /> Sign in with Google
        </button>
        <div className="sign-up">
          Don't have an account? <a href="#">Sign Up</a>
        </div>
      </div>
    </div>
  );
}
