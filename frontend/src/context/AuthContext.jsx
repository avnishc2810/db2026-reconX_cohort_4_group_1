// TICKET-ADV112 — AuthContext used by withAuth HOC; JWT persisted in memory
// (refresh path lives in HttpOnly cookie — out of scope for this trainer copy).
import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext({ user: null, login: () => {}, logout: () => {} });

export function AuthProvider({ children }) {
  // TODO(TICKET-ADV112): lazy-init `user` from sessionStorage so a page
  //                     refresh doesn't blow the JWT away. Look for keys
  //                     'reconx-token' and 'reconx-role'.
  const [user, setUser] = useState(() => {
    const token = sessionStorage.getItem('reconx-token');
    const role = sessionStorage.getItem('reconx-role');

    if (!token || !role) {
      return null;
    }

    return { token, role };
  });
  const login = (token, role) => {
    sessionStorage.setItem('reconx-token', token);
    sessionStorage.setItem('reconx-role', role);

    setUser({
      token,
      role,
    });
  };

  const logout = () => {
    sessionStorage.removeItem('reconx-token');
    sessionStorage.removeItem('reconx-role');

    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
