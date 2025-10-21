import { Component, computed, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import {Beneficio} from '../model/beneficio.model';
import {BeneficioService} from '../service/beneficio';


@Component({
  selector: 'app-beneficio',
  templateUrl: './beneficio.html',
  styleUrls: ['./beneficio.scss']
})
export class BeneficioComponent implements OnInit {

  private beneficioService = inject(BeneficioService);
  private fb = inject(FormBuilder);

  beneficios: WritableSignal<Beneficio[]> = signal([]);
  selectedBeneficio: WritableSignal<Beneficio | null> = signal(null);
  isFormVisible = signal(false);
  isTransferModalVisible = signal(false);
  notification: WritableSignal<{message: string, type: 'success' | 'error'} | null> = signal(null);

  formTitle = computed(() => this.selectedBeneficio() ? 'Editar Benefício' : 'Novo Benefício');

  beneficioForm: FormGroup;
  transferForm: FormGroup;

  constructor() {
    this.beneficioForm = this.fb.group({
      id: [null],
      nome: ['', Validators.required],
      descricao: [''],
      valor: [0, [Validators.required, Validators.min(0)]],
      version: [null]
    });

    this.transferForm = this.fb.group({
      fromId: [null, Validators.required],
      toId: [null, Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.loadBeneficios();
  }

  loadBeneficios(): void {
    this.beneficioService.listarTodos().subscribe({
      next: (data) => this.beneficios.set(data),
      error: (err) => this.showNotification('Falha ao carregar benefícios.', 'error')
    });
  }

  saveBeneficio(): void {
    if (this.beneficioForm.invalid) return;

    const beneficioData = this.beneficioForm.value;
    const operation: Observable<Beneficio> = beneficioData.id
      ? this.beneficioService.atualizar(beneficioData.id, beneficioData)
      : this.beneficioService.criar(beneficioData);

    operation.subscribe({
      next: () => {
        this.showNotification(`Benefício ${beneficioData.id ? 'atualizado' : 'criado'} com sucesso!`, 'success');
        this.resetForm();
        this.loadBeneficios();
      },
      error: (err) => this.showNotification(`Erro ao salvar: ${err.error?.message || err.message}`, 'error')
    });
  }

  deleteBeneficio(beneficio: Beneficio): void {
    if (confirm(`Tem certeza que deseja excluir o benefício "${beneficio.nome}"?`)) {
      this.beneficioService.deletar(beneficio.id).subscribe({
        next: () => {
          this.showNotification('Benefício excluído com sucesso!', 'success');
          this.loadBeneficios();
          if (this.selectedBeneficio()?.id === beneficio.id) {
            this.resetForm();
          }
        },
        error: (err) => this.showNotification(`Erro ao excluir: ${err.error?.message || err.message}`, 'error')
      });
    }
  }

  executeTransfer(): void {
    if (this.transferForm.invalid) return;

    this.beneficioService.transferir(this.transferForm.value).subscribe({
      next: () => {
        this.showNotification('Transferência realizada com sucesso!', 'success');
        this.closeTransferModal();
        this.loadBeneficios();
      },
      error: (err) => this.showNotification(`Erro na transferência: ${err.error?.message || err.message}`, 'error')
    });
  }

  selectBeneficio(beneficio: Beneficio): void {
    this.selectedBeneficio.set(beneficio);
    this.beneficioForm.patchValue(beneficio);
    this.isFormVisible.set(true);
  }

  openNewForm(): void {
    this.resetForm();
    this.isFormVisible.set(true);
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.selectedBeneficio.set(null);
    this.beneficioForm.reset();
    this.isFormVisible.set(false);
  }

  openTransferModal(): void {
    this.transferForm.reset();
    this.isTransferModalVisible.set(true);
  }

  closeTransferModal(): void {
    this.isTransferModalVisible.set(false);
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification.set({ message, type });
    setTimeout(() => this.notification.set(null), 5000);
  }

}
