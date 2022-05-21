/* eslint-disable @typescript-eslint/no-empty-function */

import React, { createContext, useState } from 'react'

export const UserContext = createContext<{
  token: string | null
  privateKey: string | null
  publicKey: string | null
  setToken: (value: string | null) => void
  setPrivateKey: (value: string | null) => void
  setPublicKey: (value: string | null) => void
}>({
  token: null,
  privateKey: null,
  publicKey: null,
  setPublicKey: () => {},
  setPrivateKey: () => {},
  setToken: () => {},
})

export const UserContextProvider: React.FC = ({ children }) => {
  const [token, setToken] = useState<string | null>(null)
  const [publicKey, setPublicKey] = useState<string | null>(null)
  const [privateKey, setPrivateKey] = useState<string | null>(null)

  return (
    <UserContext.Provider
      value={{
        token,
        setToken,
        publicKey,
        setPublicKey,
        privateKey,
        setPrivateKey,
      }}
    >
      {children}
    </UserContext.Provider>
  )
}
