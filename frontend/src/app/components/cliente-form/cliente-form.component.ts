import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-cliente-form',
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.scss']
})
export class ClienteFormComponent implements OnInit {
  clienteForm!: FormGroup;
  isLoading: boolean = false;
  
  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.initForm();
  }
  
  initForm(): void {
    this.clienteForm = this.fb.group({
      sharedKey: ['', [Validators.required, Validators.maxLength(50)]],
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      telefono: ['', [Validators.required, Validators.maxLength(20)]],
      fechaInicio: [null, Validators.required],
      fechaFin: [null, Validators.required]
    }, { validators: this.fechasValidator });
  }
  
  fechasValidator(group: FormGroup): {[key: string]: boolean} | null {
    const fechaInicio = group.get('fechaInicio')?.value;
    const fechaFin = group.get('fechaFin')?.value;
    
    if (fechaInicio && fechaFin && new Date(fechaInicio) > new Date(fechaFin)) {
      return { 'fechasInvalidas': true };
    }
    
    return null;
  }
  
  onSubmit(): void {
    if (this.clienteForm.invalid) {
      this.markFormGroupTouched(this.clienteForm);
      return;
    }
    
    this.isLoading = true;
    const cliente: Cliente = this.clienteForm.value;
    
    this.clienteService.createCliente(cliente)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          this.snackBar.open('Cliente creado exitosamente', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/clientes']);
        },
        error: (error) => {
          this.isLoading = false;
          let errorMsg = 'Error al crear el cliente';
          
          if (error.status === 400 && error.error) {
            if (error.error.message && error.error.message.includes('sharedKey')) {
              errorMsg = 'El Shared Key ya existe. Por favor, utilice otro.';
            } else if (error.error.fieldErrors) {
              errorMsg = 'Hay errores en el formulario. Por favor, revise los campos.';
            }
          }
          
          this.snackBar.open(errorMsg, 'Cerrar', { duration: 5000 });
          console.error('Error al crear cliente:', error);
        }
      });
  }
  
  // Marcar todos los campos como tocados para mostrar errores
  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as FormGroup).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }
  
  // Helpers para mostrar mensajes de error
  getErrorMessage(controlName: string): string {
    const control = this.clienteForm.get(controlName);
    
    if (control?.hasError('required')) {
      return 'Este campo es obligatorio';
    }
    
    if (control?.hasError('email')) {
      return 'Email no válido';
    }
    
    if (control?.hasError('maxlength')) {
      const maxLength = control.errors?.['maxlength'].requiredLength;
      return `Máximo ${maxLength} caracteres`;
    }
    
    return '';
  }
  
  getFechasError(): string {
    if (this.clienteForm.hasError('fechasInvalidas')) {
      return 'La fecha de inicio debe ser anterior a la fecha fin';
    }
    return '';
  }
}