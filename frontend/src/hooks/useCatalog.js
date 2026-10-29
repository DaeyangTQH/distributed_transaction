import { useMemo } from 'react'
import { listBrands } from '../api/brands'
import { listWatches } from '../api/watches'
import { useAsync } from './useAsync'

export function useCatalog() {
  const { data, error, loading, reload } = useAsync(
    () => Promise.all([listWatches(), listBrands()]).then(([watches, brands]) => ({ watches, brands })),
    [],
  )

  return useMemo(() => {
    const watches = data?.watches ?? []
    const brands = data?.brands ?? []
    const brandsById = new Map(brands.map((brand) => [brand.brandId, brand]))
    const watchesById = new Map(watches.map((watch) => [watch.watchId, watch]))
    const categories = [
      ...new Map(watches.map((watch) => [watch.categoryId, watch.categoryName])).entries(),
    ]
      .map(([categoryId, categoryName]) => ({ categoryId, categoryName }))
      .sort((a, b) => a.categoryId - b.categoryId)
    return { watches, brands, brandsById, watchesById, categories, error, loading, reload }
  }, [data, error, loading, reload])
}
