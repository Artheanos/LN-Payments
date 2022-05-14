import React, { createContext, useState } from 'react'

export const UserContext = createContext<{
  token: string | null
  setToken: (value: string | null) => void
}>({
  token: '',
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  setToken: () => {},
})

export const UserContextProvider: React.FC = ({ children }) => {
  const [token, setToken] = useState<string | null>('')

  return (
    <UserContext.Provider value={{ token, setToken }}>
      {children}
    </UserContext.Provider>
  )
}
