import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { BeneficioModel, BeneficioRequest, TransferRequest } from '../models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficiosService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/beneficios`;

  list(): Observable<BeneficioModel[]> {
    return this.http.get<BeneficioModel[]>(this.baseUrl);
  }

  get(id: number): Observable<BeneficioModel> {
    return this.http.get<BeneficioModel>(`${this.baseUrl}/${id}`);
  }

  create(payload: BeneficioRequest): Observable<BeneficioModel> {
    return this.http.post<BeneficioModel>(this.baseUrl, payload);
  }

  update(id: number, payload: BeneficioRequest): Observable<BeneficioModel> {
    return this.http.put<BeneficioModel>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  transfer(payload: TransferRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/transfer`, payload);
  }

}
