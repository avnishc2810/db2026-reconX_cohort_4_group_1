// TICKET-ADV123 — React Hook Form + Yup validation.
import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { withAuth } from '@components/withAuth.jsx';
import { api } from '@services/apiService.js';

// TODO(TICKET-ADV123): build a yup.object schema covering every field on the
//   form. Suggested validators:
//     tradeRef       — string, regex /^[A-Z]{3}-\d{8}-\d{4}$/ ("AAA-YYYYMMDD-NNNN")
//     instrumentId   — integer, positive
//     counterpartyId — integer, positive
//     assetClass     — oneOf ['EQUITY','FX','BOND','DERIVATIVE']
//     side           — oneOf ['BUY','SELL']
//     quantity       — positive number
//     price          — positive number
//     tradeDate      — date
const schema = yup.object({
  tradeRef:       yup.string().matches(/^[A-Z]{3}-\d{8}-\d{4}$/, 'tradeRef must match AAA-YYYYMMDD-NNNN').required('tradeRef is required'),
  instrumentId:   yup.number().typeError('instrumentId must be a number').integer().positive().required('instrumentId is required'),
  counterpartyId: yup.number().typeError('counterpartyId must be a number').integer().positive().required('counterpartyId is required'),
  assetClass:     yup.string().oneOf(['EQUITY','FX','BOND','DERIVATIVE']).required('assetClass is required'),
  side:           yup.string().oneOf(['BUY','SELL']).required('side is required'),
  quantity:       yup.number().typeError('quantity must be a number').positive().required('quantity is required'),
  price:          yup.number().typeError('price must be a number').positive().required('price is required'),
  tradeDate:      yup.date().typeError('tradeDate is required').required('tradeDate is required'),
});

function AddTrade() {
  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } =
        useForm({ resolver: yupResolver(schema), mode: 'onBlur' });

  async function onSubmit(values) {
    try {
      await api.createTrade(values);
      reset();
    } catch (err) {
      // surface error if needed
    }
  }

  return (
    <section>
      <h2>Add trade</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="trade-form">
        <label>Trade ref   <input {...register('tradeRef')} placeholder="EQU-20260603-0001" /></label>
        {errors.tradeRef && <p className="form-error" role="alert">{errors.tradeRef.message}</p>}

        <label>Instrument id   <input type="number" {...register('instrumentId')} /></label>
        {errors.instrumentId && <p className="form-error" role="alert">{errors.instrumentId.message}</p>}

        <label>Counterparty id <input type="number" {...register('counterpartyId')} /></label>
        {errors.counterpartyId && <p className="form-error" role="alert">{errors.counterpartyId.message}</p>}

        <label>Asset class    <select {...register('assetClass')}>
          <option value="">Select asset class</option>
          <option value="EQUITY">EQUITY</option><option value="FX">FX</option>
          <option value="BOND">BOND</option><option value="DERIVATIVE">DERIVATIVE</option>
        </select></label>
        {errors.assetClass && <p className="form-error" role="alert">{errors.assetClass.message}</p>}

        <label>Side <select {...register('side')}>
          <option value="">Select side</option>
          <option value="BUY">BUY</option><option value="SELL">SELL</option>
        </select></label>
        {errors.side && <p className="form-error" role="alert">{errors.side.message}</p>}

        <label>Quantity  <input type="number" step="0.0001" {...register('quantity')} /></label>
        {errors.quantity && <p className="form-error" role="alert">{errors.quantity.message}</p>}

        <label>Price     <input type="number" step="0.0001" {...register('price')} /></label>
        {errors.price && <p className="form-error" role="alert">{errors.price.message}</p>}

        <label>Trade date<input type="date" {...register('tradeDate')} /></label>
        {errors.tradeDate && <p className="form-error" role="alert">{errors.tradeDate.message}</p>}

        <button disabled={isSubmitting} type="submit">Submit</button>
      </form>
    </section>
  );
}

export default withAuth(AddTrade);
