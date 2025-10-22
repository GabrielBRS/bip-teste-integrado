import { AfterViewInit, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Subject, takeUntil, filter, switchMap } from 'rxjs';

import {BeneficioModel, BeneficioRequest} from '../../core/models/beneficio.model';
import { BeneficiosService } from '../../core/service/beneficio-service';
import {MatDivider} from '@angular/material/divider';
// import { ConfirmDialogComponent, ConfirmDialogData } from 'seu/caminho/confirm-dialog.component';

@Component({
  selector: 'app-beneficio',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatTooltipModule,
    MatDialogModule,
    MatSnackBarModule,
    MatDivider
  ],
  templateUrl: './beneficio.html',
  styleUrl: './beneficio.scss',
})
export class Beneficio implements OnInit, AfterViewInit, OnDestroy {

  private readonly destroy$ = new Subject<void>();

  private readonly fb = inject(FormBuilder);
  private readonly service = inject(BeneficiosService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  view: 'list' | 'form' = 'list';

  displayedColumns = ['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes'];
  dataSource = new MatTableDataSource<Beneficio>([]);
  loadingList = false;
  filterValue = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  readonly form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descricao: [''],
    valor: [0, [Validators.required, Validators.min(0)]],
    ativo: [true, Validators.required],
  });

  loadingForm = false;
  saving = false;
  isEdit = false;
  title = 'Novo Benefício';
  private currentId?: number;

  ngOnInit(): void {
    // @ts-ignore
    this.dataSource.filterPredicate = (data: BeneficioModel, filter: string) => {
      const term = filter.trim().toLowerCase();
      return (
        data.nome?.toLowerCase().includes(term) ||
        data.descricao?.toLowerCase().includes(term)
      );
    };

    // Carrega a lista inicial
    this.loadList();
  }

  ngAfterViewInit(): void {
    // Associa o paginator e o sort à tabela (só funciona se estiverem na DOM)
    // Isso pode precisar de um ViewChild dinâmico, mas para *ngIf simples,
    // o Angular geralmente lida bem se re-associarmos após a carga.
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // --- Métodos da LISTA ---

  private loadList(): void {
    this.loadingList = true;
    this.service
      .list()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (beneficios) => {
          // @ts-ignore
          this.dataSource.data = beneficios;
          if (this.paginator) this.dataSource.paginator = this.paginator;
          if (this.sort) this.dataSource.sort = this.sort;

          this.loadingList = false;
        },
        error: (err) => {
          console.error('Erro ao carregar benefícios:', err);
          this.loadingList = false;
          this.snackBar.open('Erro ao carregar lista de benefícios.', 'Fechar', { duration: 3000 });
        },
      });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.filterValue = filterValue.trim().toLowerCase();
    this.dataSource.filter = this.filterValue;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  clearFilter(): void {
    this.filterValue = '';
    this.dataSource.filter = '';
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  transfer(beneficio: BeneficioModel): void {
    // Esta ação ainda usa o Router, pois navega para uma rota diferente
    this.router.navigate(['/beneficios/transfer'], {
      queryParams: { fromId: beneficio.id },
    });
  }

  delete(beneficio: Beneficio): void {
    // Lógica de exclusão (requer um diálogo de confirmação)
    /*
    const data: ConfirmDialogData = {
      title: 'Remover benefício',
      message: `Tem certeza de que deseja remover "${beneficio.nome}"?`,
      confirmLabel: 'Remover'
    };

    this.dialog.open(ConfirmDialogComponent, { data })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed), // Apenas se 'true'
        switchMap(() => this.service.delete(beneficio.id)),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Benefício removido.', 'Fechar', { duration: 2000 });
          this.loadList(); // Recarrega a lista
        },
        error: (err) => {
          console.error('Erro ao excluir:', err);
          this.snackBar.open('Erro ao remover benefício.', 'Fechar', { duration: 3000 });
        }
      });
    */

    // if (confirm(`Tem certeza de que deseja remover "${beneficio.nome}"?`)) {
    //   this.service.delete(beneficio.id).pipe(takeUntil(this.destroy$)).subscribe(() => this.loadList());
    // }
  }

  // --- Métodos de Navegação (Troca de Visão) ---

  showCreateForm(): void {
    this.isEdit = false;
    this.title = 'Novo Benefício';
    this.currentId = undefined;
    this.form.reset({ ativo: true, valor: 0 }); // Reseta para valores padrão
    this.view = 'form';
  }

  showEditForm(beneficio: BeneficioModel): void {
    this.isEdit = true;
    this.title = 'Editar Benefício';
    this.currentId = beneficio.id;
    this.loadingForm = true; // Ativa o spinner do formulário
    this.view = 'form';

    // Busca os dados do benefício para popular o formulário
    this.service.get(beneficio.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.form.patchValue({
            nome: data.nome,
            descricao: data.descricao ?? '',
            valor: data.valor,
            ativo: data.ativo,
          });
          this.loadingForm = false;
        },
        error: (err) => {
          console.error('Erro ao carregar dados para edição:', err);
          this.snackBar.open('Erro ao carregar dados do benefício.', 'Fechar', { duration: 3000 });
          this.loadingForm = false;
          this.view = 'list'; // Volta para a lista se der erro
        },
      });
  }

  // --- Métodos do FORMULÁRIO ---

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const raw = this.form.getRawValue();

    const payload: BeneficioRequest = {
      nome: raw.nome,
      descricao: raw.descricao || undefined,
      valor: Number(raw.valor),
      ativo: raw.ativo,
    };

    const request$ =
      this.isEdit && this.currentId != null
        ? this.service.update(this.currentId, payload)
        : this.service.create(payload);

    request$.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.saving = false;
        this.snackBar.open('Benefício salvo com sucesso!', 'Fechar', {
          duration: 3000,
        });
        this.view = 'list'; // Volta para a lista
        this.loadList(); // Recarrega os dados da lista
      },
      error: (err) => {
        console.error('Erro ao salvar benefício:', err);
        this.saving = false;
        this.snackBar.open('Erro ao salvar o benefício.', 'Fechar', {
          duration: 3000,
        });
      },
    });
  }

  cancel(): void {
    this.view = 'list'; // Apenas volta para a lista
    this.form.reset(); // Limpa o formulário
  }
}
