import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'beneficio'
  },
  {
    path: 'beneficio',
    loadComponent: () =>
      import('./component/beneficio/beneficio').then((m) => m.Beneficio),
    title: 'Benefícios'
  },
  {
    path: 'beneficio/new',
    loadComponent: () =>
      import('./component/beneficio/beneficio').then((m) => m.Beneficio),
    title: 'Novo Benefício'
  },
  {
    path: 'beneficio/transfer',
    loadComponent: () =>
      import('./component/transfer/transfer').then((m) => m.Transfer),
    title: 'Transferência de Benefícios'
  },
  {
    path: 'beneficio/:id',
    loadComponent: () =>
      import('./component/beneficio/beneficio').then((m) => m.Beneficio),
    title: 'Editar Benefício'
  },
  {
    path: '**',
    redirectTo: 'beneficio'
  }
];
