import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import 'blackchalk/styles.css'
import 'blackchalk/fonts.css'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
