import { useCallback, useEffect, useState } from 'react'

export function useAsync(loader, deps = []) {
  const [state, setState] = useState({ data: null, error: null, loading: true })

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const run = useCallback(loader, deps)

  const reload = useCallback(() => {
    setState((current) => ({ ...current, loading: true, error: null }))
    return run()
      .then((data) => setState({ data, error: null, loading: false }))
      .catch((error) => setState({ data: null, error, loading: false }))
  }, [run])

  useEffect(() => {
    reload()
  }, [reload])

  return { ...state, reload }
}
