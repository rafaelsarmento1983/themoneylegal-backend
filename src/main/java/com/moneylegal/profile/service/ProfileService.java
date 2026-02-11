package com.moneylegal.profile.service;

import com.moneylegal.exception.BadRequestException;
import com.moneylegal.profile.dto.*;
import com.moneylegal.profile.model.*;
import com.moneylegal.profile.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PessoaFisicaRepository pessoaFisicaRepository;
    private final PessoaJuridicaRepository pessoaJuridicaRepository;
    private final AddressRepository addressRepository;

    // Lookups PJ
    private final PessoaJuridicaPorteEmpresaRepository pjPorteRepo;
    private final PessoaJuridicaNaturezaJuridicaRepository pjNaturezaRepo;
    private final PessoaJuridicaAtividadeCategoriaRepository pjAtvCatRepo;
    private final PessoaJuridicaAtividadeItemRepository pjAtvItemRepo;

    private final PessoaJuridicaLookupsService pessoaJuridicaLookupsService;

    private final SlugService slugService;
    private final DocumentValidationService documentValidationService;
    private final ViaCepService viaCepService;

    /**
     * PASSO 1: Escolher tipo de cadastro (PF ou PJ)
     * ✅ UPSERT:
     * - cria se não existir
     * - permite trocar tipo enquanto NÃO completou e NÃO tem endereço
     * - bloqueia troca após endereço/cadastro completo
     */
    @Transactional
    public ProfileResponseDTO chooseType(String userId, ChooseTypeRequestDTO request) {
        log.info("Choosing profile type for user: {}", userId);

        Profile.TipoCadastro newTipo = request.getTipo();
        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        if (profile == null) {
            profile = Profile.builder()
                    .userId(userId)
                    .tipo(newTipo)
                    .isCompleted(false)
                    .build();

            profile = profileRepository.save(profile);
            return buildProfileResponse(profile);
        }

        boolean hasAddress = addressRepository.existsByProfileId(profile.getId());
        if (Boolean.TRUE.equals(profile.getIsCompleted()) || hasAddress) {
            // idempotente se re-enviar o mesmo tipo
            if (profile.getTipo() == newTipo) return buildProfileResponse(profile);

            throw new BadRequestException(
                    "Cadastro finalizado: não é possível alterar o tipo de pessoa após cadastrar o endereço."
            );
        }

        if (profile.getTipo() != newTipo) {
            // limpa dados antigos para evitar inconsistência
            if (profile.isPessoaFisica()) {
                pessoaFisicaRepository.findByProfileId(profile.getId())
                        .ifPresent(pessoaFisicaRepository::delete);
            } else if (profile.isPessoaJuridica()) {
                pessoaJuridicaRepository.findByProfileId(profile.getId())
                        .ifPresent(pessoaJuridicaRepository::delete);
            }

            profile.setTipo(newTipo);
            profile.setSlug(null); // evita slug incoerente
            profile = profileRepository.save(profile);
        }

        return buildProfileResponse(profile);
    }

    /**
     * PASSO 2A: Completar dados de Pessoa Física (UPSERT)
     */
    @Transactional
    public ProfileResponseDTO completePessoaFisica(String userId, CompletePessoaFisicaRequestDTO request) {
        log.info("Completing Pessoa Fisica for user: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Perfil não encontrado"));

        if (!profile.isPessoaFisica()) {
            throw new BadRequestException("Perfil não é do tipo Pessoa Física");
        }

        String cpfDigits = request.getCpf().replaceAll("\\D", "");
        documentValidationService.validateCpf(cpfDigits);

        PessoaFisica pessoaFisica = pessoaFisicaRepository.findByProfileId(profile.getId()).orElse(null);

        if (pessoaFisica == null || !request.getCpf().equals(pessoaFisica.getCpf())) {
            if (pessoaFisicaRepository.existsByCpf(request.getCpf())) {
                throw new BadRequestException("CPF já cadastrado");
            }
        }

        if (profile.getSlug() == null || profile.getSlug().isBlank()) {
            profile.setSlug(slugService.generateUniqueSlug(request.getNomeCompleto()));
            profileRepository.save(profile);
        }

        if (pessoaFisica == null) {
            pessoaFisica = PessoaFisica.builder()
                    .profileId(profile.getId())
                    .build();
        }

        pessoaFisica.setNomeCompleto(request.getNomeCompleto());
        pessoaFisica.setCpf(request.getCpf());
        pessoaFisica.setDataNascimento(request.getDataNascimento());
        pessoaFisica.setTelefone(request.getTelefone());

        pessoaFisicaRepository.save(pessoaFisica);
        return buildProfileResponse(profile);
    }

    /**
     * PASSO 2B: Completar dados de Pessoa Jurídica (UPSERT)
     * ✅ agora recebe IDs e valida no banco
     */
    @Transactional
    public ProfileResponseDTO completePessoaJuridica(String userId, CompletePessoaJuridicaRequestDTO request) {
        log.info("Completing Pessoa Juridica for user: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Perfil não encontrado"));

        if (!profile.isPessoaJuridica()) {
            throw new BadRequestException("Perfil não é do tipo Pessoa Jurídica");
        }

        String cnpjDigits = request.getCnpj().replaceAll("\\D", "");
        documentValidationService.validateCnpj(cnpjDigits);

        PessoaJuridica pessoaJuridica = pessoaJuridicaRepository.findByProfileId(profile.getId()).orElse(null);

        if (pessoaJuridica == null || !request.getCnpj().equals(pessoaJuridica.getCnpj())) {
            if (pessoaJuridicaRepository.existsByCnpj(request.getCnpj())) {
                throw new BadRequestException("CNPJ já cadastrado");
            }
        }

        // ✅ valida IDs
        pessoaJuridicaLookupsService.requirePorteAtivo(request.getPorteEmpresaId());
        pessoaJuridicaLookupsService.requireNaturezaAtiva(request.getNaturezaJuridicaId());
        pessoaJuridicaLookupsService.requireAtividadeItemAtivo(request.getAtividadeItemId());

        if (profile.getSlug() == null || profile.getSlug().isBlank()) {
            profile.setSlug(slugService.generateUniqueSlug(request.getRazaoSocial()));
            profileRepository.save(profile);
        }

        if (pessoaJuridica == null) {
            pessoaJuridica = PessoaJuridica.builder()
                    .profileId(profile.getId())
                    .build();
        }

        pessoaJuridica.setRazaoSocial(request.getRazaoSocial());
        pessoaJuridica.setNomeFantasia(request.getNomeFantasia());
        pessoaJuridica.setCnpj(request.getCnpj());
        pessoaJuridica.setInscricaoEstadual(request.getInscricaoEstadual());
        pessoaJuridica.setInscricaoMunicipal(request.getInscricaoMunicipal());
        pessoaJuridica.setDataFundacao(request.getDataFundacao());

        // ✅ IDs (CHAR(36) no banco)
        pessoaJuridica.setPorteEmpresaId(request.getPorteEmpresaId());
        pessoaJuridica.setNaturezaJuridicaId(request.getNaturezaJuridicaId());
        pessoaJuridica.setAtividadeItemId(request.getAtividadeItemId());

        pessoaJuridica.setTelefone(request.getTelefone());
        pessoaJuridica.setNomeResponsavel(request.getNomeResponsavel());
        pessoaJuridica.setEmailResponsavel(request.getEmailResponsavel());

        pessoaJuridicaRepository.save(pessoaJuridica);
        return buildProfileResponse(profile);
    }

    /**
     * PASSO 3: Completar endereço (UPSERT + finaliza)
     */
    @Transactional
    public ProfileResponseDTO completeAddress(String userId, CompleteAddressRequestDTO request) {
        log.info("Completing address for user: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Perfil não encontrado"));

        boolean hasPessoaData = profile.isPessoaFisica()
                ? pessoaFisicaRepository.findByProfileId(profile.getId()).isPresent()
                : pessoaJuridicaRepository.findByProfileId(profile.getId()).isPresent();

        if (!hasPessoaData) {
            throw new BadRequestException("Complete os dados pessoais antes de adicionar endereço");
        }

        Address address = addressRepository.findByProfileId(profile.getId())
                .orElseGet(() -> Address.builder().profileId(profile.getId()).build());

        address.setCep(request.getCep());
        address.setLogradouro(request.getLogradouro());
        address.setNumero(request.getNumero());
        address.setComplemento(request.getComplemento());
        address.setBairro(request.getBairro());
        address.setCidade(request.getCidade());
        address.setEstado(request.getEstado());
        address.setPais(request.getPais());

        addressRepository.save(address);

        if (!Boolean.TRUE.equals(profile.getIsCompleted())) {
            profile.setIsCompleted(true);
            profileRepository.save(profile);
        }

        return buildProfileResponse(profile);
    }

    public Optional<ProfileResponseDTO> getMyProfile(String userId) {
        log.info("Getting profile for user: {}", userId);

        return profileRepository.findByUserId(userId)
                .map(this::buildProfileResponse);
    }

    public ViaCepResponseDTO consultarCep(String cep) {
        return viaCepService.consultarCep(cep);
    }

    private ProfileResponseDTO buildProfileResponse(Profile profile) {
        ProfileResponseDTO response = ProfileResponseDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .tipo(profile.getTipo() != null ? profile.getTipo().name() : null)
                .slug(profile.getSlug())
                .avatarUrl(profile.getAvatarUrl())
                .isCompleted(profile.getIsCompleted())
                .build();

        if (profile.isPessoaFisica()) {
            pessoaFisicaRepository.findByProfileId(profile.getId())
                    .ifPresent(pf -> response.setPessoaFisica(ProfileResponseDTO.PessoaFisicaDTO.fromEntity(pf)));
        }

        if (profile.isPessoaJuridica()) {
            pessoaJuridicaRepository.findByProfileId(profile.getId()).ifPresent(pj -> {

                PessoaJuridicaPorteEmpresa porte = null;
                PessoaJuridicaNaturezaJuridica natureza = null;
                PessoaJuridicaAtividadeItem atvItem = null;
                PessoaJuridicaAtividadeCategoria atvCat = null;

                if (pj.getPorteEmpresaId() != null) {
                    porte = pjPorteRepo.findById(pj.getPorteEmpresaId()).orElse(null);
                }
                if (pj.getNaturezaJuridicaId() != null) {
                    natureza = pjNaturezaRepo.findById(pj.getNaturezaJuridicaId()).orElse(null);
                }
                if (pj.getAtividadeItemId() != null) {
                    atvItem = pjAtvItemRepo.findById(pj.getAtividadeItemId()).orElse(null);
                    if (atvItem != null && atvItem.getCategoryId() != null) {
                        atvCat = pjAtvCatRepo.findById(atvItem.getCategoryId()).orElse(null);
                    }
                }

                response.setPessoaJuridica(
                        ProfileResponseDTO.PessoaJuridicaDTO.fromEntity(pj, porte, natureza, atvCat, atvItem)
                );
            });
        }

        addressRepository.findByProfileId(profile.getId())
                .ifPresent(addr -> response.setAddress(ProfileResponseDTO.AddressDTO.fromEntity(addr)));

        return response;
    }
}
