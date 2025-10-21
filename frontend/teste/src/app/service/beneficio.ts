import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Beneficio} from '../model/beneficio.model';


@Injectable({
  providedIn: 'root'
})
export class BeneficioService {

  private http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/backend-module/api/v1/beneficios';

  listarTodos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`);
  }

  criar(beneficio: Partial<Beneficio>): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio);
  }

  atualizar(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, beneficio);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  transferir(request: { fromId: number, toId: number, amount: number }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transferir`, request);
  }

}
