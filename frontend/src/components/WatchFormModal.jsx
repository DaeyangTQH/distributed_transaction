import { useState } from 'react'
import {
  SketchButton,
  SketchButtonGroup,
  SketchFormField,
  SketchInput,
  SketchModal,
  SketchSelect,
  space,
} from 'blackchalk'
import ErrorAlert from './ErrorAlert'
import { createWatch, updateWatch } from '../api/watches'

const EMPTY_FORM = {
  name: '',
  brandId: '',
  categoryId: '',
  caseMaterial: '',
  strapMaterial: '',
  waterResistance: '',
  price: '',
  stock: '',
}

function toForm(watch) {
  if (!watch) return EMPTY_FORM
  return {
    name: watch.name,
    brandId: String(watch.brandId),
    categoryId: String(watch.categoryId),
    caseMaterial: watch.caseMaterial ?? '',
    strapMaterial: watch.strapMaterial ?? '',
    waterResistance: watch.waterResistance ?? '',
    price: String(watch.price),
    stock: String(watch.stock),
  }
}

function Field({ label, error, children }) {
  return (
    <div className={error ? 'app-error' : undefined}>
      <SketchFormField label={label} error={error}>
        {children}
      </SketchFormField>
    </div>
  )
}

function validate(form) {
  const errors = {}
  if (form.name.trim().length <= 10) errors.name = 'Name must be longer than 10 characters'
  if (!form.brandId) errors.brandId = 'Pick a brand'
  if (!form.categoryId) errors.categoryId = 'Pick a category'
  if (!form.caseMaterial.trim()) errors.caseMaterial = 'Case material is required'
  if (!form.strapMaterial.trim()) errors.strapMaterial = 'Strap material is required'
  if (!form.waterResistance.trim()) errors.waterResistance = 'Water resistance is required'
  if (form.price === '' || Number.isNaN(Number(form.price)) || Number(form.price) < 100)
    errors.price = 'Price must be at least 100'
  if (form.stock === '' || !Number.isInteger(Number(form.stock)) || Number(form.stock) < 0)
    errors.stock = 'Stock must be a whole number, 0 or more'
  return errors
}

function WatchForm({ watch, brands, categories, onCancel, onSaved }) {
  const isEdit = Boolean(watch)
  const [form, setForm] = useState(() => toForm(watch))
  const [errors, setErrors] = useState({})
  const [submitError, setSubmitError] = useState(null)
  const [saving, setSaving] = useState(false)

  const field = (key) => ({
    value: form[key],
    onChange: (value) => setForm((current) => ({ ...current, [key]: value })),
  })

  const handleSubmit = async () => {
    const nextErrors = validate(form)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    const payload = {
      name: form.name.trim(),
      brandId: Number(form.brandId),
      categoryId: Number(form.categoryId),
      caseMaterial: form.caseMaterial.trim(),
      strapMaterial: form.strapMaterial.trim(),
      waterResistance: form.waterResistance.trim(),
      price: Number(form.price),
      stock: Number(form.stock),
    }

    setSaving(true)
    setSubmitError(null)
    try {
      if (isEdit) await updateWatch(watch.watchId, payload)
      else await createWatch(payload)
      onSaved(isEdit ? `Updated ${payload.name}.` : `Created ${payload.name}.`)
    } catch (err) {
      setSubmitError(err.message)
      setSaving(false)
    }
  }

  const brandOptions = [
    { value: '', label: 'Select a brand' },
    ...brands.map((brand) => ({ value: String(brand.brandId), label: brand.brandName })),
  ]
  const categoryOptions = [
    { value: '', label: 'Select a category' },
    ...categories.map((category) => ({ value: String(category.categoryId), label: category.categoryName })),
  ]

  return (
    <form
      className="watch-form"
      noValidate
      onSubmit={(event) => {
        event.preventDefault()
        handleSubmit()
      }}
      style={{
        width: 'min(820px, calc(90vw - 64px))',
        display: 'flex',
        flexDirection: 'column',
        gap: space[4],
      }}
    >
      {submitError && (
        <ErrorAlert title="Could not save" onClose={() => setSubmitError(null)}>
          {submitError}
        </ErrorAlert>
      )}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, minmax(0, 1fr))',
          gap: `${space[3]} ${space[4]}`,
        }}
      >
        <div style={{ gridColumn: 'span 2' }}>
          <Field label="Name" error={errors.name}>
            <SketchInput {...field('name')} placeholder="Rolex Submariner Date" aria-label="Name" />
          </Field>
        </div>
        <Field label="Stock" error={errors.stock}>
          <SketchInput {...field('stock')} type="number" placeholder="10" aria-label="Stock" />
        </Field>
        <Field label="Brand" error={errors.brandId}>
          <SketchSelect
            options={brandOptions}
            selected={form.brandId}
            onChange={field('brandId').onChange}
            aria-label="Brand"
          />
        </Field>
        <Field label="Category" error={errors.categoryId}>
          <SketchSelect
            options={categoryOptions}
            selected={form.categoryId}
            onChange={field('categoryId').onChange}
            aria-label="Category"
          />
        </Field>
        <Field label="Price ($)" error={errors.price}>
          <SketchInput {...field('price')} type="number" placeholder="9500" aria-label="Price" />
        </Field>
        <Field label="Case material" error={errors.caseMaterial}>
          <SketchInput {...field('caseMaterial')} placeholder="Stainless steel" aria-label="Case material" />
        </Field>
        <Field label="Strap material" error={errors.strapMaterial}>
          <SketchInput {...field('strapMaterial')} placeholder="Oystersteel bracelet" aria-label="Strap material" />
        </Field>
        <Field label="Water resistance" error={errors.waterResistance}>
          <SketchInput {...field('waterResistance')} placeholder="300m" aria-label="Water resistance" />
        </Field>
      </div>
      <SketchButtonGroup>
        <SketchButton variant="primary" disabled={saving}>
          {saving ? 'Saving…' : isEdit ? 'Save changes' : 'Create watch'}
        </SketchButton>
        <SketchButton
          variant="secondary"
          onClick={(event) => {
            event.preventDefault()
            onCancel()
          }}
        >
          Cancel
        </SketchButton>
      </SketchButtonGroup>
    </form>
  )
}

/** `watch` is the watch being edited; pass `null` with `open` to create a new one. */
export default function WatchFormModal({ open, watch, brands, categories, onClose, onSaved }) {
  return (
    <SketchModal open={open} onClose={onClose} title={watch ? `Edit watch #${watch.watchId}` : 'New watch'}>
      {open && (
        <WatchForm
          key={watch?.watchId ?? 'new'}
          watch={watch}
          brands={brands}
          categories={categories}
          onCancel={onClose}
          onSaved={onSaved}
        />
      )}
    </SketchModal>
  )
}
