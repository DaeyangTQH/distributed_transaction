import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import {
  SketchButton,
  SketchForm,
  SketchFormField,
  SketchIcon,
  SketchInput,
  SketchList,
  SketchListItem,
  SketchSplitLayout,
  SketchText,
  space,
} from 'blackchalk'
import ErrorAlert from '../components/ErrorAlert'
import { useAuth } from '../context/AuthContext'

const DEMO_ACCOUNTS = [
  { username: 'adminsys', label: 'Administrator' },
  { username: 'staff01', label: 'Staff' },
  { username: 'member01', label: 'Member' },
]
const DEMO_PASSWORD = '@1'

export default function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  if (user) return <Navigate to="/" replace />

  const handleSubmit = async () => {
    if (!username || !password) {
      setError('Enter both username and password')
      return
    }
    setSubmitting(true)
    setError(null)
    try {
      await login(username, password)
      navigate(location.state?.from ?? '/', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <SketchSplitLayout
      ratio={[3, 2]}
      left={
        <div style={{ display: 'flex', flexDirection: 'column', gap: space[4] }}>
          <SketchText variant="title" as="h1">
            Log in
          </SketchText>
          <SketchText variant="lead">Sign in through account-service to place and track orders.</SketchText>
          {error && (
            <ErrorAlert title="Login failed" onClose={() => setError(null)}>
              {error}
            </ErrorAlert>
          )}
          <SketchForm onSubmit={handleSubmit}>
            <SketchFormField label="Username">
              <SketchInput value={username} onChange={setUsername} placeholder="member01" aria-label="Username" />
            </SketchFormField>
            <SketchFormField label="Password">
              <div style={{ position: 'relative' }}>
                <SketchInput
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={setPassword}
                  placeholder="Password"
                  aria-label="Password"
                  className="password-input"
                />
                <button
                  type="button"
                  className="sketch-interactive"
                  onClick={() => setShowPassword((shown) => !shown)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                  aria-pressed={showPassword}
                  title={showPassword ? 'Hide password' : 'Show password'}
                  style={{
                    position: 'absolute',
                    top: '50%',
                    right: 10,
                    transform: 'translateY(-50%)',
                    display: 'grid',
                    placeItems: 'center',
                    padding: 4,
                    border: 'none',
                    background: 'none',
                    color: 'var(--sketch-fg)',
                    cursor: 'pointer',
                  }}
                >
                  <SketchIcon name="eye" size={20} />
                  {showPassword && (
                    <span
                      aria-hidden="true"
                      style={{
                        position: 'absolute',
                        width: 24,
                        height: 2,
                        background: 'currentColor',
                        transform: 'rotate(-45deg)',
                      }}
                    />
                  )}
                </button>
              </div>
            </SketchFormField>
            <SketchButton variant="primary" disabled={submitting}>
              {submitting ? 'Signing in…' : 'Log in'}
            </SketchButton>
          </SketchForm>
        </div>
      }
      right={
        <div style={{ display: 'flex', flexDirection: 'column', gap: space[3] }}>
          <SketchText variant="heading" as="h2">
            Demo accounts
          </SketchText>
          <SketchText variant="caption">{`Every seeded account uses the password ${DEMO_PASSWORD}. Pick one to fill the form.`}</SketchText>
          <SketchList>
            {DEMO_ACCOUNTS.map((account) => (
              <SketchListItem
                key={account.username}
                active={username === account.username}
                onClick={() => {
                  setUsername(account.username)
                  setPassword(DEMO_PASSWORD)
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: space[3] }}>
                  <SketchText variant="body">{account.username}</SketchText>
                  <SketchText variant="caption">{account.label}</SketchText>
                </div>
              </SketchListItem>
            ))}
          </SketchList>
        </div>
      }
    />
  )
}
